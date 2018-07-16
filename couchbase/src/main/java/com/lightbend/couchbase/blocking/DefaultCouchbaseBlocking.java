package com.lightbend.couchbase.blocking;

import akka.actor.ExtendedActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.couchbase.client.core.utils.Blocking;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;

import java.util.concurrent.TimeUnit;

public class DefaultCouchbaseBlocking implements CouchbaseBlocking {
    private final LoggingAdapter log;

    private final DefaultCouchbaseEnvironment environment;
    private final CouchbaseBlockingConfig couchbaseConfig;

    private final CouchbaseCluster clusterBlocking;
    private final Bucket bucketBlocking;

    public DefaultCouchbaseBlocking(ExtendedActorSystem system) {
        this.log = Logging.getLogger(system, getClass().getName());
        this.environment = DefaultCouchbaseEnvironment.create();
        this.couchbaseConfig = new CouchbaseBlockingConfig(system);

        this.clusterBlocking = couchbaseConfig.createBlockingCluster(environment);
        this.bucketBlocking = couchbaseConfig.openBlockingBucket(clusterBlocking);
    }

    @Override
    public CouchbaseCluster getCluster() {
        return clusterBlocking;
    }

    @Override
    public Bucket getBucket() {
        return bucketBlocking;
    }

    void shutdown() {
        //TODO handle errors properly and add logging
        bucketBlocking.close();
        clusterBlocking.disconnect();

        Blocking.blockForSingle(environment.shutdownAsync().single(), 30, TimeUnit.SECONDS);
    }

}