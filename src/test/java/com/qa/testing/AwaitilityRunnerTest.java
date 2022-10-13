package com.qa.testing;

import org.awaitility.Awaitility;
import org.awaitility.Durations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.*;
import static org.hamcrest.Matchers.equalTo;

@RunWith(JUnit4.class)
public class AwaitilityRunnerTest {
    private AsyncService asyncService;

    @Before
    public void setUp() {
        asyncService = new AsyncService();
    }

    @Test
    public void basicTestAwaility() {
        /*El estado se obtiene mediante un Callable que sondea nuestro servicio a
        intervalos definidos (100 ms por defecto) después de un retraso inicial
        especificado (por defecto 100 ms) y una espera maxima por defecto de 10 segundos*/

        asyncService.initialize();//Se inicializa el servicio
        await().until(asyncService::isInitialized);// usamos await , uno de los metodos estáticos de la clase Awaitility
    }

    @Test
    public void testingAwaitilityWithSetDefault() {

        Awaitility.setDefaultPollInterval(200, TimeUnit.MILLISECONDS); //el intervalo que pregunta
        Awaitility.setDefaultPollDelay(Duration.ZERO); //el retraso
        Awaitility.setDefaultTimeout(Durations.FIVE_SECONDS); //tiempo de espera

        asyncService.initialize();

        await()
                .atLeast(Durations.ONE_HUNDRED_MILLISECONDS) //Al menos en
                .atMost(Durations.FIVE_SECONDS) //Como mucho
                .with()
                .until(asyncService::isInitialized);
    }

    @Test
    public void testingUsingMatchers() {
        long value = 5;

        asyncService.initialize();
        await().until(asyncService::isInitialized);
        asyncService.addValue(value);

        await().until(asyncService::getValue, equalTo(value + 1));
    }

    @Test
    public void testingIgnoringExceptions() {
        /*A veces, tenemos una situación en la que un método lanza una excepción
        antes de que se realice un trabajo asíncrono. En nuestro servicio, puede
        ser una llamada al método getValue antes de que se inicialice el servicio*/

        asyncService.initialize();
        given().ignoreException(IllegalStateException.class)
                .await()
                .atMost(Durations.FIVE_SECONDS)
                .atLeast(Durations.FIVE_HUNDRED_MILLISECONDS)
                .until(asyncService::getValue, equalTo(0L));
    }

    @Test
    public void testingAccessingPrivateFields(){
        /*Awaitility puede incluso acceder a campos privados
        para realizar afirmaciones sobre ellos*/
        asyncService.initialize();
        await()
                .until(fieldIn(asyncService)
                        .ofType(boolean.class)
                        .andWithName("initialized"), equalTo(true));
    }
}