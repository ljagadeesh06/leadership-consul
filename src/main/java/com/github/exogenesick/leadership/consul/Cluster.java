package com.github.exogenesick.leadership.consul;

import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.KeyValueClient;
import com.ecwid.consul.v1.kv.KeyValueConsulClient;
import com.ecwid.consul.v1.kv.model.PutParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.exogenesick.leadership.consul.config.ClusterConfiguration;

public class Cluster {
    private Session session;
    private ClusterConfiguration clusterConfiguration;
    private KeyValueClient consulKVClient;

    Cluster(
        Session session,
        ClusterConfiguration clusterConfiguration
    ) {
        this.session = session;
        this.clusterConfiguration = clusterConfiguration;
        this.consulKVClient = new KeyValueConsulClient();
    }

    Cluster(
        Session session,
        ClusterConfiguration clusterConfiguration,
        KeyValueClient consulKVClient
    ) {
        this.session = session;
        this.clusterConfiguration = clusterConfiguration;
        this.consulKVClient = consulKVClient;
    }

    public boolean claimLeadership() throws JsonProcessingException {
        final String key =
            String.format(clusterConfiguration.getLeaderKeyTemplate(), clusterConfiguration.getServiceName());

        PutParams params = new PutParams();
        params.setAcquireSession(session.getId());

        Response response = consulKVClient.setKVValue(key, buildLeaderData(session), params);

        return (boolean) response.getValue();
    }

    private String buildLeaderData(Session session) throws JsonProcessingException {
        LeaderData leaderData = new LeaderData();
        leaderData.sessionId = session.getId();
        leaderData.serviceName = clusterConfiguration.getServiceName();
        leaderData.serviceId = clusterConfiguration.getServiceId();

        return new ObjectMapper().writeValueAsString(leaderData);
    }

    public ClusterConfiguration getClusterConfiguration() {
        return clusterConfiguration;
    }
}