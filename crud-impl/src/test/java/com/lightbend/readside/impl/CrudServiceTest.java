package com.lightbend.readside.impl;

import akka.japi.function.Procedure;
import com.lightbend.couchbase.test.CouchbaseMockLagomTest;
import com.lightbend.lagom.javadsl.testkit.ServiceTest;
import com.lightbend.readside.api.CrudService;
import com.lightbend.readside.api.GreetingMessage;
import org.junit.Test;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.defaultSetup;
import static com.lightbend.lagom.javadsl.testkit.ServiceTest.withServer;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

public class CrudServiceTest extends CouchbaseMockLagomTest {

    private void withMyServer(Procedure<ServiceTest.TestServer> block) {
        withServer(defaultSetup().configureBuilder(withCouchbaseMock()).withCassandra(false), block);
    }

    @Test
    public void shouldStorePersonalizedGreeting() {
        withMyServer(server -> {
            CrudService service = server.client(CrudService.class);

            String msg1 = service.hello("Alex").invoke().toCompletableFuture().get(5, SECONDS);
            assertEquals("Hello (default), Alex!", msg1);

            service.useGreeting("Alex").invoke(new GreetingMessage("Hi")).toCompletableFuture().get(5, SECONDS);
            String msg2 = service.hello("Alex").invoke().toCompletableFuture().get(5, SECONDS);
            assertEquals("Hi, Alex!", msg2);
        });
    }

}