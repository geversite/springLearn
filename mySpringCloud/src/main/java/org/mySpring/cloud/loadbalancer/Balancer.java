package org.mySpring.cloud.loadbalancer;

import java.util.List;

public interface Balancer {
    public String next();

    public List<String> getIPs();
}
