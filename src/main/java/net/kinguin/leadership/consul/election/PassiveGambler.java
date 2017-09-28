package net.kinguin.leadership.consul.election;

import rx.Observable;

public class PassiveGambler implements Gambler {
    @Override
    public boolean isLeader() {
        return true;
    }

    @Override
    public Observable<Object> asObservable() {
        return Observable.just(Gambler.ELECTED_FIRST_TIME);
    }
}
