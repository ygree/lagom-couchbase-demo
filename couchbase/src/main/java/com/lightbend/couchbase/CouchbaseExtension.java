package com.lightbend.couchbase;

import akka.actor.*;

public class CouchbaseExtension extends AbstractExtensionId<Couchbase> implements ExtensionIdProvider {

    public static final CouchbaseExtension CouchbaseExtensionProvider = new CouchbaseExtension();

    private CouchbaseExtension() {}

    @Override
    public ExtensionId<? extends Extension> lookup() {
        return CouchbaseExtensionProvider;
    }

    @Override
    public Couchbase createExtension(ExtendedActorSystem system) {
        DefaultCouchbase couchbase = new DefaultCouchbase(system);
        system.registerOnTermination(couchbase::shutdown);
        return couchbase;
    }
}
