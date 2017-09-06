package com.github.exogenesick.leadership.consul.mode;

public class SingleMode implements ClusterMode {
    @Override
    public boolean isLeader() {
        return true;
    }
}