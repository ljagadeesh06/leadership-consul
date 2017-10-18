package net.kinguin.leadership.consul.election;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PassiveGamblerTest {
    private PassiveGambler passiveGambler;

    @Before
    public void setUp() throws Exception {
        passiveGambler = new PassiveGambler();
    }

    @Test
    public void should_emit_first_time_leader_elected_event() throws Exception {
        passiveGambler.asObservable()
            .toBlocking()
            .subscribe(i -> {
                Info n = (Info) i;
                assertEquals(Gambler.ELECTED_FIRST_TIME, ((Info) i).status);
            });
    }

    @Test
    public void should_always_return_leader_elected() throws Exception {
        assertTrue(passiveGambler.isLeader());
    }
}