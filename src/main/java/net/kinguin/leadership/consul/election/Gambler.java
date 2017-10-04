package net.kinguin.leadership.consul.election;

import rx.Observable;

public interface Gambler {
    static final String ELECTED_FIRST_TIME = "elected.first";
    static final String ELECTED = "elected";

    boolean isLeader();
    Observable<Object> asObservable();
}