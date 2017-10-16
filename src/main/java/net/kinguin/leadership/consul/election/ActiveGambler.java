package net.kinguin.leadership.consul.election;

import com.ecwid.consul.v1.kv.KeyValueClient;
import com.ecwid.consul.v1.kv.model.PutParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.kinguin.leadership.consul.config.ClusterConfiguration;
import rx.Observable;
import rx.subjects.PublishSubject;

import java.io.IOException;

public class ActiveGambler implements Runnable, Gambler {
    private KeyValueClient consulKVClient;
    private String sessionId;
    private ClusterConfiguration clusterConfiguration;
    private boolean gotLeadership = false;
    private boolean wasLeader = false;
    private PublishSubject<Object> publisher = PublishSubject.create();
    private String key;
    private ObjectMapper mapper = new ObjectMapper();

    public ActiveGambler(
        KeyValueClient consulKVClient,
        String sessionId,
        ClusterConfiguration clusterConfiguration
    ) {
        this.consulKVClient = consulKVClient;
        this.sessionId = sessionId;
        this.clusterConfiguration = clusterConfiguration;
        key = String.format(clusterConfiguration.getElection().getEnvelopeTemplate(),
            clusterConfiguration.getServiceName());
    }

    @Override
    public void run() {
        try {
            gotLeadership = vote();
        } catch (JsonProcessingException e) {
            gotLeadership = false;
            publish(e);
            return;
        }

        if (false == gotLeadership) {
            publish(Gambler.NOT_ELECTED);

            if (true == wasLeader) {
                publish(Gambler.RELEGATION);
                wasLeader = false;
            }

            return;
        }

        publish(Gambler.ELECTED);

        if (false == wasLeader) {
            publish(Gambler.ELECTED_FIRST_TIME);
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
        PutParams params = new PutParams();
        params.setAcquireSession(sessionId);

        return consulKVClient.setKVValue(key, createVoteEnvelope(), params)
            .getValue();
    }

    private String createVoteEnvelope() throws JsonProcessingException {
        Vote vote = new Vote();
        vote.sessionId = sessionId;
        vote.serviceName = clusterConfiguration.getServiceName();
        vote.serviceId = clusterConfiguration.getServiceId();

        return new ObjectMapper().writeValueAsString(vote);
    }

    private Vote leaderLookup() throws IOException {
        String response = consulKVClient.getKVValue(key)
            .getValue()
            .getDecodedValue();

        return mapper.readValue(response, Vote.class);
    }

    private void publish(String status) {
        Info info = new Info();
        info.status = status;

        try {
            info.vote = leaderLookup();
        } catch (IOException e) {}

        publisher.onNext(info);
    }

    private void publish(Exception e) {
        Info info = new Info();
        info.status = Gambler.ERROR;
        info.error = e.getMessage();

        try {
            info.vote = leaderLookup();
        } catch (IOException ioe) {}

        publisher.onNext(info);
    }
}
