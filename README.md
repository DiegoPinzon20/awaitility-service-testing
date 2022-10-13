# Awaitility Service Testing

Repositorio con pruebas automatizadas de servicios asincronos utilizando Awaitility para el manejo de las esperas.
## Documentación Oficial Awaitility

Puede encontrar la documentación oficial de la libreria en [Awaitility](http://www.awaitility.org/).

## Awaitility con hilos de ejecución(Thread)

Se hace proporcionando un Thread (proveedor) o ExecutorService que se usara cuando Awaility sondee la condicion. Tenga
en cuenta que esta es una caracteristica avanzada y debe usarse con moderacion.

``` java
given().pollThread(Thread::new).await().atMost(1000, MILLISECONDS).until(...);
``` 

Otra forma es especificar un ExecutorService para que Awaitility lo utilice:

``` java
ExecutorSerivce es = ...
given().pollExecutorService(es).await().atMost(1000, MILLISECONDS).until(..);
``` 

Finalmente, puede indicarle a Awaitility que trabaje sobre el mismo hilo de la tarea que está sondeando.

``` java
with().pollInSameThread().await().atMost(1000, MILLISECONDS).until(...);
``` 

Este enfoque puede usarse para sondear tareas de **Serenity BDD**, para que vaya en el mismo hilo de ejecucion.

``` java
        private int someTask(){
            actor.attemptsTo(WaitUntil.the(SomePage.SUB_TITLE, WebElementStateMatchers.isCurrentlyVisible()));
            //el resto del codigo va debajo//
            return 0;
        }

        @Test
        public void awaitTest(){
            Awaitility.await().pollInSameThread().atMost(300, TimeUnit.SECONDS)
                            .pollInterval( 3, TimeUnit.SECONDS )
                            .until( () -> someTask() > 1);
        }
```