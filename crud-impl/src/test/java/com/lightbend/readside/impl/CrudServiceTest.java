package com.lightbend.readside.impl;

import com.lightbend.couchbase.test.CouchbaseMockLagomTest;
import com.lightbend.readside.api.CrudService;
import com.lightbend.readside.api.GreetingMessage;
import org.junit.Test;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.defaultSetup;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

public class CrudServiceTest extends CouchbaseMockLagomTest {

    @Test
    public void shouldStorePersonalizedGreeting() {
        withServerAndCouchbaseMock(defaultSetup().withCassandra(false), server -> {
            CrudService service = server.client(CrudService.class);

            String msg1 = service.hello("Alex").invoke().toCompletableFuture().get(5, SECONDS);
            assertEquals("Hello (default), Alex!", msg1);

            service.useGreeting("Alex").invoke(new GreetingMessage("Hi")).toCompletableFuture().get(5, SECONDS);
            String msg2 = service.hello("Alex").invoke().toCompletableFuture().get(5, SECONDS);
            assertEquals("Hi, Alex!", msg2);
        });
    }

}