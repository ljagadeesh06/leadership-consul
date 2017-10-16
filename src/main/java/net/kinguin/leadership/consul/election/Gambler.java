package net.kinguin.leadership.consul.election;

import rx.Observable;

public interface Gambler {
    String ELECTED_FIRST_TIME = "elected.first";
    String ELECTED = "elected";
    String RELEGATION = "relegation";
    String NOT_ELECTED = "notelected";
    String ERROR = "error";

    boolean isLeader();
    Observable<Object> asObservable();
}