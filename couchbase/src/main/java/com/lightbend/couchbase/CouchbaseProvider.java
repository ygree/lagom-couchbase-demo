package com.lightbend.couchbase;

import akka.actor.ActorSystem;
import com.google.inject.Provider;

import javax.inject.Inject;

public class CouchbaseProvider implements Provider<Couchbase> {

    private final ActorSystem system;

    @Inject
    public CouchbaseProvider(ActorSystem system) {
        this.system = system;
    }

    @Override
    public Couchbase get() {
        return CouchbaseExtension.CouchbaseExtensionProvider.get(system);
    }
}
