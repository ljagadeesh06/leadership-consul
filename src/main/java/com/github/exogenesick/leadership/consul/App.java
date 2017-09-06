package com.github.exogenesick.leadership.consul;

import com.github.exogenesick.leadership.consul.config.ClusterConfiguration;
import com.github.exogenesick.leadership.consul.mode.MultiMode;

public class App {
    public static void main(String[] args) {
        Session session = new Session(15);
        Cluster cluster = new Cluster(session, new ClusterConfiguration());

        new MultiMode(cluster, 5);
    }
}
