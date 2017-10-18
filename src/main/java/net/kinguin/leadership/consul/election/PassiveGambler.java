package net.kinguin.leadership.consul.election;

import rx.Observable;

public class PassiveGambler implements Gambler {
    @Override
    public boolean isLeader() {
        return true;
    }

    @Override
    public Observable<Object> asObservable() {
        Info info = new Info();
        info.status = Gambler.ELECTED_FIRST_TIME;

        return Observable.just(info);
    }
}
