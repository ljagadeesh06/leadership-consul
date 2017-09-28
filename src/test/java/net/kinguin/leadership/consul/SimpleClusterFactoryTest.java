package net.kinguin.leadership.consul;

import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.KeyValueConsulClient;
import com.ecwid.consul.v1.session.SessionConsulClient;
import net.kinguin.leadership.consul.factory.SimpleClusterFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Observable;
import rx.observers.TestSubscriber;

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
        clusterFactory.mode(SimpleClusterFactory.MODE_SINGLE)
            .build();
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
            .build();

        // then
        activeGambler.subscribe(subscriber);
        subscriber.assertNoErrors();
    }
}