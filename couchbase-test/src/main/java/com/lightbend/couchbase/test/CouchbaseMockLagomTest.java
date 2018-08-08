package com.lightbend.couchbase.test;

import com.lightbend.couchbase.Couchbase;
import play.inject.guice.GuiceApplicationBuilder;

import java.util.function.Function;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.bind;

/**
 * The base-class for testing Lagom services with Couchbase Mock.
 * For non-Lagom related data access services see {@link CouchbaseMockTest}.
 */
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
