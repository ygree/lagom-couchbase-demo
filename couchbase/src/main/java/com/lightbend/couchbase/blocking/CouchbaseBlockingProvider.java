package com.lightbend.couchbase.blocking;

import akka.actor.ActorSystem;
import com.google.inject.Provider;

import javax.inject.Inject;

public class CouchbaseBlockingProvider implements Provider<CouchbaseBlocking> {

    private final ActorSystem system;

    @Inject
    public CouchbaseBlockingProvider(ActorSystem system) {
        this.system = system;
    }

    @Override
    public CouchbaseBlocking get() {
        return (CouchbaseBlocking) CouchbaseBlockingExtension.CouchbaseExtensionProvider.get(system);
    }
}
