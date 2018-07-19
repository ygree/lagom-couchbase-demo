package com.lightbend.couchbase.test;

import com.lightbend.couchbase.Couchbase;
import play.inject.guice.GuiceApplicationBuilder;

import java.util.function.Function;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.bind;

abstract public class CouchbaseMockLagomTest extends CouchbaseMockTest {

    /**
     * Use with Lagom TestKit withServer
     *
     * ```
     *  withServer(defaultSetup()
     *                 .configureBuilder(withCouchbaseMock())
     *                 ...
     * ```
     */
    protected Function<GuiceApplicationBuilder, GuiceApplicationBuilder> withCouchbaseMock() {
        return b -> b.overrides(bind(Couchbase.class).toInstance(getCouchbase()));
    }
}
