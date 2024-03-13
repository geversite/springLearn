package org.mySpring.cloud.eureka;

import lombok.SneakyThrows;
import org.mySpring.annotation.Controller;
import org.mySpring.annotation.ResponseBody;
import org.mySpring.web.annotation.RequestMapping;
import org.myTomcat.entity.HttpRequest;
import org.mylog.Logger;

import java.util.*;

@Controller
public class ServerController {

    Map<String, List<String>> service2Host = new HashMap<>();
    Map<String, List<String>> host2Service = new HashMap<>();
    Map<String,Boolean> heartBeat = new HashMap<>();

    Logger log = Logger.getLogger();

    long period = EurekaLib.period();

    public ServerController(){
        new Timer().schedule(new Worker(),3000,period);

    }


    @RequestMapping("/registerService")
    @ResponseBody
    public boolean clientRegister(String rpcName,int port, HttpRequest request){
        String host = request.getRemoteHost();
        String address = host+":"+ port;
        if(!service2Host.containsKey(rpcName)){
            service2Host.put(rpcName, new ArrayList<>());
        }
        if(!host2Service.containsKey(host)){
            host2Service.put(address, new ArrayList<>());
        }
        if(!service2Host.get(rpcName).contains(address)){
            register(rpcName, address);
        }else {
            heartBeat.put(address,true);
        }
        return true;
    }

    @RequestMapping("/renewalService")
    @ResponseBody
    public void clientRenewal(HttpRequest request, int port){
        String ip = request.getRemoteHost();
        String host = ip+":"+ port;
        heartBeat.put(host,true);
    }

    @RequestMapping("/logoffService")
    @ResponseBody
    public void clientLogoff(HttpRequest request, int port){
        String ip = request.getRemoteHost();
        String host = ip+":"+ port;
        clientLogoff(host);
    }

    private void clientLogoff(String host){
        for (String s : host2Service.get(host)) {
            log.info(s+"on"+host+"is removed.");
            service2Host.get(s).remove(host);
        }
        if(host2Service.containsKey(host)){
            host2Service.remove(host);
            heartBeat.remove(host);
        }
    }

    private void register(String rpcName, String address) {
        service2Host.get(rpcName).add(address);
        host2Service.get(address).add(rpcName);
        heartBeat.put(address,true);
    }

    @RequestMapping("/getService")
    @ResponseBody
    public String getService(String rpcName){
        return service2Host.get(rpcName).get(0);
    }



    class Worker extends TimerTask{

        @SneakyThrows
        @Override
        public void run() {
            log.debug("--------Routine Check------------");
            for (Map.Entry<String, Boolean> e : heartBeat.entrySet()) {
                if(!e.getValue()){
                    log.info("--------"+e.getKey()+"Lost, Logging off------------");
                    clientLogoff(e.getKey());
                }else {
                    heartBeat.put(e.getKey(),false);
                }
            }
        }
    }

}
