package com.lightbend.couchbase;

import com.google.inject.AbstractModule;
import com.lightbend.couchbase.blocking.CouchbaseBlocking;
import com.lightbend.couchbase.blocking.CouchbaseBlockingProvider;

public class CouchbaseModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Couchbase.class).toProvider(CouchbaseProvider.class);
        bind(CouchbaseBlocking.class).toProvider(CouchbaseBlockingProvider.class);
    }
}
