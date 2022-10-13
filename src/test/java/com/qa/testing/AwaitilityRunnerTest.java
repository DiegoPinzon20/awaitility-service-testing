package com.qa.testing;

import org.awaitility.Awaitility;
import org.awaitility.Durations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.*;
import static org.awaitility.pollinterval.FibonacciPollInterval.fibonacci;
import static org.hamcrest.Matchers.equalTo;

@RunWith(JUnit4.class)
public class AwaitilityRunnerTest {
    private AsyncService asyncService;

    @Before
    public void setUp() {
        asyncService = new AsyncService();
        Awaitility.reset();
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

    @Test(timeout = 2000)
    public void testingAccessingPrivateFields() {
        /*Awaitility puede incluso acceder a campos privados
        para realizar afirmaciones sobre ellos*/
        asyncService.initialize();
        await()
                .until(fieldIn(asyncService)
                        .ofType(boolean.class)
                        .andWithName("initialized"), equalTo(true));

//        await().until( fieldIn(asyncService).ofType(boolean.class), equalTo(true) );
//        await().until( fieldIn(asyncService).ofType(int.class).andAnnotatedWith(MyAnnotation.class), equalTo(2) );
    }

    @Test
    public void testingFibonacciPollIntervalDefaultMilliseconds() {
        /*FibonacciPollInterval genera un intervalo de sondeo no lineal basado en la secuencia de Fibonacci
         * Esto generara un intervalo de sondeo de 1, 1, 2, 3, 5, 8, 13, ... milisegundos */

        asyncService.initialize();
        with().pollInterval(fibonacci()).await().until(
                fieldIn(asyncService)
                        .ofType(boolean.class)
                        .andWithName("initialized"), equalTo(true)
        );
    }

    @Test
    public void testingFibonacciPollIntervalSeconds() {
        //FibonacciPollInterval genera un intervalo de sondeo no lineal basado en la secuencia de Fibonacci

        asyncService.initialize();
        with().pollInterval(fibonacci(SECONDS)).await().until(
                fieldIn(asyncService)
                        .ofType(boolean.class)
                        .andWithName("initialized"), equalTo(true)
        );
    }

    @Test
    public void testingFibonacciPollIntervalWithOffset() {
        /*Desplazamiento significa que la secuencia de Fibonacci se
        inicia a partir de este desplazamiento (por defecto, el desplazamiento es 0). El desplazamiento también puede ser negativo (-1) para comenzar con 0 ( fib(0)= 0).
         */

        asyncService.initialize();

        with().pollInterval(fibonacci().with().unit(SECONDS).and().offset(5))
                .await().until(
                        fieldIn(asyncService)
                                .ofType(boolean.class)
                                .andWithName("initialized"), equalTo(true));
    }
}