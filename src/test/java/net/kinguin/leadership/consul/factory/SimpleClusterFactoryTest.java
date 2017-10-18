package net.kinguin.leadership.consul.factory;

import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.KeyValueConsulClient;
import com.ecwid.consul.v1.session.SessionConsulClient;
import net.kinguin.leadership.consul.election.Gambler;
import net.kinguin.leadership.consul.election.Info;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SimpleClusterFactoryTest {
    private SimpleClusterFactory clusterFactory;

    @Mock
    private SessionConsulClient sessionConsulClient;

    @Mock
    private KeyValueConsulClient keyValueConsulClient;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        clusterFactory = new SimpleClusterFactory(sessionConsulClient, keyValueConsulClient);
    }

    @Test
    public void should_return_passive_gambler() throws Exception {
        Gambler gambler = clusterFactory.mode(SimpleClusterFactory.MODE_SINGLE)
            .build();

        gambler.asObservable()
            .toBlocking()
            .subscribe(i -> {
                Info n = (Info) i;
                assertEquals(Gambler.ELECTED_FIRST_TIME, ((Info) i).status);
            });
    }

    @Test
    public void should_return_active_gambler() throws Exception {
        // given
        TestSubscriber<Object> subscriber = new TestSubscriber<>();
        String expectedSessionId = "expected-session-id-12345";
        Response response = new Response(expectedSessionId, 1L, true, 2L);

        when(sessionConsulClient.sessionCreate(any(), any()))
            .thenReturn(response);

        // when
        Observable activeGambler = clusterFactory.mode(SimpleClusterFactory.MODE_MULTI)
            .build()
            .asObservable();

        // then
        activeGambler.subscribe(subscriber);
        subscriber.assertNoErrors();
    }
}