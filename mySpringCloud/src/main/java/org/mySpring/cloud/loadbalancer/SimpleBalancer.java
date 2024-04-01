package org.mySpring.cloud.loadbalancer;

import java.util.List;

public class SimpleBalancer implements Balancer{

    List<String> ips;
    int cnt=0;

    public SimpleBalancer(List<String> ips){
        this.ips = ips;
    }

    @Override
    public String next() {
        String ip = ips.get(cnt);
        cnt=(cnt++)% ips.size();
        return ip;
    }

    @Override
    public List<String> getIPs() {
        return ips;
    }
}
