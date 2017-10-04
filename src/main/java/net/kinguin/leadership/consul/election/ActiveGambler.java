package net.kinguin.leadership.consul.election;

import com.ecwid.consul.v1.kv.KeyValueClient;
import com.ecwid.consul.v1.kv.model.PutParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.kinguin.leadership.consul.config.ClusterConfiguration;
import rx.Observable;
import rx.subjects.PublishSubject;

public class ActiveGambler implements Runnable, Gambler {
    private KeyValueClient consulKVClient;
    private String sessionId;
    private ClusterConfiguration clusterConfiguration;
    private boolean gotLeadership = false;
    private boolean wasLeader = false;
    private PublishSubject<Object> publisher = PublishSubject.create();

    public ActiveGambler(
        KeyValueClient consulKVClient,
        String sessionId,
        ClusterConfiguration clusterConfiguration
    ) {
        this.consulKVClient = consulKVClient;
        this.sessionId = sessionId;
        this.clusterConfiguration = clusterConfiguration;
    }

    @Override
    public void run() {
        try {
            gotLeadership = vote();
        } catch (JsonProcessingException e) {
            publisher.onError(e);
            gotLeadership = false;
            return;
        }

        publisher.onNext(new java.lang.String(Gambler.ELECTED));

        if (false == wasLeader) {
            publisher.onNext(new java.lang.String(Gambler.ELECTED_FIRST_TIME));
            wasLeader = true;
        }
    }

    public synchronized boolean isLeader() {
        return gotLeadership;
    }

    public Observable<Object> asObservable() {
        return publisher;
    }

    private boolean vote() throws JsonProcessingException {
        final java.lang.String key =
            java.lang.String.format(clusterConfiguration.getElection().getEnvelopeTemplate(), clusterConfiguration.getServiceName());

        PutParams params = new PutParams();
        params.setAcquireSession(sessionId);

        return consulKVClient.setKVValue(key, createVoteEnvelope(), params)
            .getValue();
    }

    private java.lang.String createVoteEnvelope() throws JsonProcessingException {
        Vote vote = new Vote();
        vote.sessionId = sessionId;
        vote.serviceName = clusterConfiguration.getServiceName();
        vote.serviceId = clusterConfiguration.getServiceId();

        return new ObjectMapper().writeValueAsString(vote);
    }
}
