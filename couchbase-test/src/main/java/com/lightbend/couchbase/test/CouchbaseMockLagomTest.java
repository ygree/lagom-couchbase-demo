package com.lightbend.couchbase.test;

import akka.japi.function.Procedure;
import com.lightbend.couchbase.Couchbase;
import com.lightbend.lagom.javadsl.testkit.ServiceTest;
import play.inject.guice.GuiceApplicationBuilder;

import java.util.function.Function;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.bind;
import static com.lightbend.lagom.javadsl.testkit.ServiceTest.withServer;

/**
 * The base-class for testing Lagom services with Couchbase Mock.
 * For non-Lagom related data access services see {@link CouchbaseMockTest}.
 */
abstract public class CouchbaseMockLagomTest extends CouchbaseMockTest {

    /**
     * Use to test Lagom service
     *
     * ```
     *  withServerAndCouchbaseMock(defaultSetup(), server -> {
     *                 ...
     * ```
     */
    protected void withServerAndCouchbaseMock(ServiceTest.Setup setup, Procedure<ServiceTest.TestServer> block) {
        withServer(setup.configureBuilder(withCouchbaseMock()), block);
    }

    /**
     * Use with Lagom TestKit withServer
     *
     * ```
     *  withServer(defaultSetup().configureBuilder(withCouchbaseMock()), server -> {
     *                 ...
     * ```
     */
    protected Function<GuiceApplicationBuilder, GuiceApplicationBuilder> withCouchbaseMock() {
        return b -> b.overrides(bind(Couchbase.class).toInstance(getCouchbase()));
    }
}
