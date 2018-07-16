package com.lightbend.couchbase.blocking;

import akka.actor.*;

public class CouchbaseBlockingExtension extends AbstractExtensionId<CouchbaseBlocking> implements ExtensionIdProvider {

    public static final CouchbaseBlockingExtension CouchbaseExtensionProvider = new CouchbaseBlockingExtension();

    private CouchbaseBlockingExtension() {}

    @Override
    public ExtensionId<? extends Extension> lookup() {
        return CouchbaseExtensionProvider;
    }

    @Override
    public CouchbaseBlocking createExtension(ExtendedActorSystem system) {
        DefaultCouchbaseBlocking couchbase = new DefaultCouchbaseBlocking(system);
        system.registerOnTermination(couchbase::shutdown);
        return couchbase;
    }
}
