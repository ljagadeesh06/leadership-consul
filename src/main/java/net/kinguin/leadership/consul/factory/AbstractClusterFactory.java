package net.kinguin.leadership.consul.factory;

import rx.Observable;

public abstract class AbstractClusterFactory {
    public static final String MODE_SINGLE = "single";
    public static final String MODE_MULTI = "multi";
    protected String mode;

    abstract public Observable build();
}
