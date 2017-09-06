package com.github.exogenesick.leadership.consul.mode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.exogenesick.leadership.consul.Cluster;

public class MultiMode implements Runnable, ClusterMode {
    private Cluster cluster;
    private long attemptsFrequencySeconds;
    private boolean gotLeadership;

    public MultiMode(
        Cluster cluster,
        long attemptsFrequencySeconds
    ) {
        this.cluster = cluster;
        this.attemptsFrequencySeconds = attemptsFrequencySeconds;

        try {
            upkeep();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void upkeep() throws InterruptedException {
        Thread upkeep = new Thread(this);
        upkeep.setDaemon(true);
        upkeep.start();
    }

    public synchronized void run() {
        while (true) {
            try {
                wait(attemptsFrequencySeconds * 1000);
                gotLeadership = cluster.claimLeadership();
            } catch (JsonProcessingException e) {
                gotLeadership = false;
            } catch (InterruptedException e) {}
        }
    }

    public synchronized boolean isLeader() {
        return gotLeadership;
    }
}
