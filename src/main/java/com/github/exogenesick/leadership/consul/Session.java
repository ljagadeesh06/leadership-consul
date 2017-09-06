package com.github.exogenesick.leadership.consul;

import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.session.SessionConsulClient;
import com.ecwid.consul.v1.session.model.NewSession;

public class Session implements Runnable {
    private SessionConsulClient sessionClient;
    private int ttlSeconds;
    private String id;

    public Session(int ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
        this.sessionClient = new SessionConsulClient();

        id = createSession();
        try {
            upkeep();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Session(int ttlSeconds, SessionConsulClient sessionClient) {
        this.ttlSeconds = ttlSeconds;
        this.sessionClient = sessionClient;

        id = createSession();
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
        upkeep.join();
    }

    public synchronized void run() {
        while (true) {
            try {
                wait(ttlSeconds / 2 * 1000);
            } catch (InterruptedException e) {
                System.out.println(e);
            }

            sessionClient.renewSession(getId(), null);
        }
    }

    public synchronized String getId() {
        return id;
    }

    private String createSession() {
        NewSession consulSession = new NewSession();
        consulSession.setTtl(String.format("%d%s", ttlSeconds, "s"));

        Response response = sessionClient.sessionCreate(consulSession, null);

        return response.getValue().toString();
    }
}
