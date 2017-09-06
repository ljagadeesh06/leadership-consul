package com.github.exogenesick.leadership.consul.config;

public class ClusterConfiguration {
    private String host;
    private int port;
    private String serviceName = "cluster-app";
    private String serviceId = "node-1";
    private int sessionTtl = 15;
    private int leadershipAttemptsInterval = 5;
    private String leaderKeyTemplate = "services/%s/leader";

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public int getSessionTtl() {
        return sessionTtl;
    }

    public void setSessionTtl(int sessionTtl) {
        this.sessionTtl = sessionTtl;
    }

    public int getLeadershipAttemptsInterval() {
        return leadershipAttemptsInterval;
    }

    public void setLeadershipAttemptsInterval(int leadershipAttemptsInterval) {
        this.leadershipAttemptsInterval = leadershipAttemptsInterval;
    }

    public String getLeaderKeyTemplate() {
        return leaderKeyTemplate;
    }

    public void setLeaderKeyTemplate(String leaderKeyTemplate) {
        this.leaderKeyTemplate = leaderKeyTemplate;
    }
}
