package org.mySpring.context;


import org.mySpring.annotation.*;
import org.mySpring.aop.Aspect;
import org.mySpring.aop.PointCutHandler;
import org.mySpring.aop.PointCutParser;
import org.mySpring.aop.ProxyBeanBuilder;
import org.mySpring.boot.AutoConfigImportSelector;
import org.mySpring.boot.Conditional;
import org.mySpring.boot.ConditionalOnClass;
import org.mySpring.lib.BeanLib;
import org.mySpring.lib.PathCal;
import org.mylog.Logger;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext {

    private Class<?> config;

    List<String> basePackages = new ArrayList<>();

    public ConcurrentHashMap<String, BeanDefinition> getBeanDefinitionDict() {
        return beanDefinitionDict;
    }

    private final ConcurrentHashMap<String, BeanDefinition> beanDefinitionDict = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Object> singletonPool = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, Object> singletonPoolCache = new ConcurrentHashMap<>();
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    private final HashMap<String,BeanDefinition> beanPostProcessorNames = new HashMap<>();

    private final Map<Method, PointCutHandler> aspectMap = new HashMap<>();

    private final Set<Class<?>> aspectTarget = new HashSet<>();

    private AutoConfigImportSelector selector;

    protected static Logger log = Logger.getLogger();

    public ApplicationContext(){
    }

    public ApplicationContext(String[] basePackages) throws Exception {
        this.basePackages = Arrays.asList(basePackages);
        for(String str: basePackages){
            scan(str);
        }
        if (selector != null) {
            autoConfig(selector);
        }
        initSingleton();
        log.info("mySpring.ApplicationContext: Container init finished!");
    }
    public ApplicationContext(Class<?> config) throws Exception {
        this.config = config;
        ClassMetaData metaData = new ClassMetaData(config);
        ComponentScan componentScan = metaData.getAnnotation(ComponentScan.class);
        String path = componentScan.value();
        if (path.equals("")){
            path = config.getName();
            path = path.substring(0,path.lastIndexOf('.'));
        }
        if(metaData.isAnnotationPresent(Configuration.class)){
            register(config);
        }
        scanDir(path);
        if(selector!=null){
            autoConfig(selector);
        }

        initSingleton();

        log.info("mySpring.ApplicationContext: Container init finished!");
    }

    private void autoConfig(AutoConfigImportSelector selector) throws Exception {
        if(selector==null) return;
        String[] classes = selector.selectImports();
        for (String aClass : classes) {
            Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(aClass);
            register(clazz);
        }
    }

    public Object getBean(String beanName) {
        if(beanDefinitionDict.get(beanName)==null){
            throw new RuntimeException("beanName "+beanName+" not exist!");
        }else {
            BeanDefinition definition = beanDefinitionDict.get(beanName);
            if(definition.getScope().equals("singleton")){
                Object o = singletonPool.get(beanName);
                if(o!=null){
                    return o;
                }else {
                    o = singletonPoolCache.get(beanName);
                }
                if(o!=null){
                    return o;
                }else {
                    return createBean(beanName, definition);
                }
            }else {
                return createBean(beanName, definition);
            }
        }
    }

    public <T> T getBean(String beanName, Class<T> clazz) {
        Object o = getBean(beanName);
        if(clazz.isInstance(o)){
            return (T) o;
        }else {
            throw new RuntimeException("beanType not matched!");
        }
    }

    public <T> Map<String, T> getBeansOfType(Class<T> type) {
        Map<String, T> map = new HashMap<>();
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionDict.entrySet()) {
            if(type.isAssignableFrom(entry.getValue().clazz)){
                map.put(entry.getKey(), getBean(entry.getKey(),type));
            }
        }
        return map;
    }

    public <T> T getBeanOfType(Class<T> type) {
        Map<String, T> map = getBeansOfType(type);
        if (map.size()!=1){
            throw new RuntimeException();
        }
        return map.values().stream().findFirst().get();
    }


    protected void initSingleton() {
        for (String beanName: beanPostProcessorNames.keySet()){
            BeanDefinition definition = beanDefinitionDict.get(beanName);
            if(definition.scope.equals("singleton")){
                getBean(beanName);
            }
        }
        for(String beanName: beanDefinitionDict.keySet()){
            BeanDefinition definition = beanDefinitionDict.get(beanName);
            if(definition.scope.equals("singleton")){
                getBean(beanName);
            }
        }
    }

    protected void scanDir(String path) throws Exception {
        path = path.replace('.','/').trim();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(path);
        File file = new File(url.getFile());
        if(!file.isDirectory()){
            log.warn("Component is not a dir!\n");
        }else {
            for(File dir: file.listFiles()){
                if(dir.isDirectory()){
                    basePackages.add(PathCal.getClassPathRelativePath(dir, loader));
                }
            }
            for(File dir: file.listFiles()){
                if(dir.isDirectory()){
                    scan(path+"/"+dir.getName());
                }
            }
        }
    }

    protected void scan(String path) throws Exception {

        path = path.replace('.','/').trim();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(path);
        File file = new File(url.getFile());
        if(!file.isDirectory()){
            log.warn("Component is not a dir!\n");
        }else {
            for (File file1 : file.listFiles()) {
                String fileName = file1.getName();
                fileName = fileName.substring(0,fileName.lastIndexOf("."));
                fileName = (path+"."+fileName).replace('/','.');
                try {
                    Class<?> clazz = loader.loadClass(fileName);
                    register(clazz);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void register(Class<?> clazz) throws Exception{


        ClassMetaData metaData = new ClassMetaData(clazz);
        if(metaData.isAnnotationPresent(Import.class)){
            for (Class<?> c : (List<Class<?>>) metaData.getAnnotationValue(Import.class)) {
                register(c);
            }
        }
        String bn = "";
        if(metaData.isAnnotationPresent(Component.class)){
            if(metaData.isAnnotationValuePresent(Component.class)){
                bn = (String) metaData.getAnnotationValue(Component.class);
            }
            String beanName = BeanLib.getBeanName(clazz);
            if(!bn.equals("")){
                beanName = bn;
            }
            Scope scopeAnno = clazz.getDeclaredAnnotation(Scope.class);
            String scope = "singleton";
            if(scopeAnno!=null && "prototype".equals(scopeAnno.value())){
                scope = "prototype";
            }
            BeanDefinition beanDefinition = new BeanDefinition(clazz,scope);
            if(beanDefinitionDict.containsKey(beanName)){
                throw new Exception("beanName collide.");
            }
            beanDefinitionDict.put(beanName,beanDefinition);
            if(BeanPostProcessor.class.isAssignableFrom(clazz)){
                beanPostProcessorNames.put(beanName, beanDefinition);
            }
            if(metaData.isAnnotationPresent(Configuration.class)){
                scanConfigure(clazz, beanName);
            }
            if (metaData.isAnnotationPresent(Aspect.class)){
                PointCutParser.parseAspect(clazz, beanName, aspectMap, aspectTarget);
            }
        }
        if (BeanRegistrar.class.isAssignableFrom(clazz)){
            Map<String, BeanDefinition> map = ((BeanRegistrar)clazz.getDeclaredConstructor().newInstance()).registerList(config,this);
            beanDefinitionDict.putAll(map);
        }
        if (AutoConfigImportSelector.class.isAssignableFrom(clazz)){
            selector = (AutoConfigImportSelector) clazz.getDeclaredConstructor().newInstance();
        }

    }

    private void scanConfigure(Class<?> clazz, String builderName) throws Exception{

        for (Method method : clazz.getDeclaredMethods()) {
            Class<?> beanClass = method.getReturnType();
            if(validBeanMethod(method)){
                String beanName = BeanLib.getBeanName(beanClass);
                String bn = method.getAnnotation(Bean.class).value();
                if(!bn.equals("")){
                    beanName = bn;
                }
                Scope scopeAnno = method.getDeclaredAnnotation(Scope.class);
                String scope = "singleton";
                if(scopeAnno!=null && "prototype".equals(scopeAnno.value())){
                    scope = "prototype";
                }
                BeanDefinition beanDefinition = new BeanDefinition(beanClass,scope);
                beanDefinition.setBuilder(new BeanBuilder(builderName, method, null));
                if(beanDefinitionDict.containsKey(beanName)){
                    throw new Exception("beanName collide.");
                }
                beanDefinitionDict.put(beanName,beanDefinition);
                if (method.getReturnType().isAnnotationPresent(Aspect.class)){
                    PointCutParser.parseAspect(method.getReturnType(), beanName , aspectMap, aspectTarget);
                }
            }
        }
    }


    private Object createBean(String beanName, BeanDefinition definition){
        try {
            Object newInstance;
            if (definition.getBuilder()!=null){
                newInstance = definition.getBuilder().method.invoke(getBean(definition.getBuilder().getBeanName()),definition.getBuilder().getParams());
            }else if (aspectTarget.contains(definition.clazz)){
                newInstance = ProxyBeanBuilder.build(definition, aspectMap, this);
            }else {
                newInstance = definition.getClazz().getDeclaredConstructor().newInstance();
            }
            if (definition.getScope().equals("singleton")){
                singletonPoolCache.put(beanName, newInstance);
            }
            dependInjection(newInstance);
            newInstance = initBean(beanName, newInstance);
            if (definition.getScope().equals("singleton")){
                singletonPoolCache.remove(beanName);
                singletonPool.put(beanName, newInstance);
                if(BeanPostProcessor.class.isAssignableFrom(definition.getClazz())){
                    beanPostProcessors.add((BeanPostProcessor) newInstance);
                }
            }
            return newInstance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void dependInjection(Object newInstance) throws IllegalAccessException {
        boolean CGLibProxy = newInstance.getClass().getName().contains("$$");
        Field[] fields = CGLibProxy?newInstance.getClass().getSuperclass().getDeclaredFields():newInstance.getClass().getDeclaredFields();
        for (Field field : fields) {
            if(field.isAnnotationPresent(Autowired.class)){
                Autowired autowiredAnno = field.getDeclaredAnnotation(Autowired.class);
                String abeanName = field.getType().getName();
                if(!"".equals(autowiredAnno.value())){
                    abeanName = autowiredAnno.value();
                }
                Object o = getBean(abeanName);
                field.setAccessible(true);
                field.set(newInstance,o);
            }
        }
    }

    private Object initBean(String beanName, Object newInstance){



        if (newInstance instanceof ApplicationContextAware){
            ((ApplicationContextAware)newInstance).setApplicationContext(this);
        }
        if(newInstance instanceof BeanNameAware){
            ((BeanNameAware) newInstance).setBeanName(beanName);
        }

        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            newInstance = beanPostProcessor.postProcessBeforeInitialization(beanName, newInstance);
        }

        if(newInstance instanceof InitializingBean){
            ((InitializingBean) newInstance).afterPropertiesSet();
        }

        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            newInstance = beanPostProcessor.postProcessAfterInitialization(beanName, newInstance);
        }
        return newInstance;
    }

    private boolean validBeanMethod(Method method){
        if(!method.isAnnotationPresent(Bean.class)){
            return false;
        }
        if(!method.isAnnotationPresent(ConditionalOnClass.class) && !method.isAnnotationPresent(Conditional.class)){
            return true;
        }
        if(method.isAnnotationPresent(ConditionalOnClass.class)){
            try{
                Thread.currentThread().getContextClassLoader().loadClass(method.getAnnotation(ConditionalOnClass.class).value());
                return true;
            }catch (Exception ignored){
                return false;
            }
        }
        if(!method.isAnnotationPresent(Conditional.class)){
            return true;
        }
        try {
            return method.getAnnotation(Conditional.class).value().getDeclaredConstructor().newInstance().matches(beanDefinitionDict);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }


    public List<String> getBasePackages() {
        return basePackages;
    }
}
