package com.lightbend.couchbase;

import akka.actor.ExtendedActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.couchbase.client.core.utils.Blocking;
import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.CouchbaseAsyncCluster;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import rx.Observable;

import java.util.concurrent.TimeUnit;

public class DefaultCouchbase implements Couchbase {
    private final DefaultCouchbaseEnvironment environment;
    private final CouchbaseConfig couchbaseConfig;
    private final CouchbaseAsyncCluster cluster;
    private final AsyncBucket bucket;
    private final LoggingAdapter log;

    public DefaultCouchbase(ExtendedActorSystem system) {
        this.log = Logging.getLogger(system, getClass().getName());
        this.environment = DefaultCouchbaseEnvironment.create();
        this.couchbaseConfig = new CouchbaseConfig(system);
        this.cluster = couchbaseConfig.createCluster(environment);
        this.bucket = couchbaseConfig.openBucket(cluster).toSingle().toBlocking().value();
    }

    @Override
    public CouchbaseEnvironment getEnvironment() {
        return environment;
    }

    @Override
    public AsyncBucket getBucket() {
        return bucket;
    }

    void shutdown() {
        //TODO handle errors properly and add logging
        Blocking.blockForSingle(bucket.close(), 30, TimeUnit.SECONDS);
        Blocking.blockForSingle(cluster.disconnect(), 30, TimeUnit.SECONDS);
        Blocking.blockForSingle(environment.shutdownAsync().single(), 30, TimeUnit.SECONDS);
    }
}