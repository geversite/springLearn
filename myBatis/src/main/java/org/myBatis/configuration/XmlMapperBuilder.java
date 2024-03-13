package org.myBatis.configuration;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class XmlMapperBuilder {

    Configuration configuration;

    public XmlMapperBuilder(Configuration configuration){
        this.configuration =configuration;
    }

    public void parse(InputStream stream) throws Exception {
        SAXReader reader = new SAXReader();
        Document document = reader.read(stream);
        Element root = document.getRootElement();
        String namespace = root.attribute("namespace").getValue();

        List<Element> selectList = root.selectNodes("//select");
        List<Element> insertList = root.selectNodes("//insert");
        List<Element> updateList = root.selectNodes("//update");
        List<Element> deleteList = root.selectNodes("//delete");
        List<Element> allList = new ArrayList<>();
        allList.addAll(selectList);
        allList.addAll(insertList);
        allList.addAll(updateList);
        allList.addAll(deleteList);
        for(Element element: allList){
            String id = element.attributeValue("id");
            String key = namespace + "." +id;
            String method = element.getName();
            Method mappedMethod = null;
            Method[] methods = Thread.currentThread().getContextClassLoader().loadClass(namespace).getMethods();
            for (Method method1 : methods) {
                if (method1.getName().equals(id)){
                    mappedMethod = method1;
                }
            }
            if(mappedMethod==null){
                throw new Exception("Mapping method "+key+" failed:method not found");
            }
            String resultType = element.attributeValue("resultType");
            String paramType = element.attributeValue("parameterType");
            String sql = element.getTextTrim();
            MappedStatement statement = new MappedStatement();
            statement.setId(key);
            statement.setMethod(mappedMethod);
            statement.setSqlMethod(method);
            statement.setResultType(resultType);
            statement.setParameterType(paramType);
            statement.setSql(sql);
            configuration.getMappedStatements().put(key,statement);
        }



    }
}
