package net.kinguin.leadership.consul.election;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class PassiveGamblerTest {
    private PassiveGambler passiveGambler;

    @Before
    public void setUp() throws Exception {
        passiveGambler = new PassiveGambler();
    }

    @Test
    public void should_emit_first_time_leader_elected_event() throws Exception {
        assertTrue(passiveGambler.asObservable()
            .map(String::valueOf)
            .toBlocking()
            .single()
            .equals(Gambler.ELECTED_FIRST_TIME));
    }

    @Test
    public void should_always_return_leader_elected() throws Exception {
        assertTrue(passiveGambler.isLeader());
    }
}