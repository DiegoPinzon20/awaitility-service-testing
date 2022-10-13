package com.qa.testing;

import org.awaitility.Awaitility;
import org.awaitility.Durations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@RunWith(JUnit4.class)
public class AwaitilityRunnerTest {
    private AsyncService asyncService;

    @Before
    public void setUp() {
        asyncService = new AsyncService();
    }

    @Test
    public void testingAwaitility() {
        /*El estado se obtiene mediante un Callable que sondea nuestro servicio a
        intervalos definidos (100 ms por defecto) después de un retraso inicial
        especificado (por defecto 100 ms)*/

        Awaitility.setDefaultPollInterval(1000, TimeUnit.MILLISECONDS); //el intervalo que pregunta
        Awaitility.setDefaultPollDelay(Duration.ZERO); //el retraso
        Awaitility.setDefaultTimeout(Durations.TEN_MINUTES); //tiempo de espera

        asyncService.initialize();//Se inicializa el servicio

        /*// usamos await , uno de los metodos estáticos de la clase Awaitility
        await().until(asyncService::isInitialized);*/

        await()
                .atLeast(Durations.ONE_HUNDRED_MILLISECONDS) //Al menos en
                .atMost(Durations.FIVE_SECONDS) //Como mucho
                .with()
                .pollInterval(Durations.ONE_HUNDRED_MILLISECONDS)
                .until(asyncService::isInitialized);
    }
}