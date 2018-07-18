package com.lightbend.couchbase;

import com.google.inject.AbstractModule;

public class CouchbaseModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Couchbase.class).toProvider(CouchbaseProvider.class);
    }
}
