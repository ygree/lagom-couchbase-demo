package com.lightbend.couchbase;

import akka.actor.ExtendedActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;

import java.util.concurrent.TimeUnit;

public class DefaultCouchbase implements Couchbase {
    private final DefaultCouchbaseEnvironment environment;
    private final CouchbaseConfig couchbaseConfig;
    private final CouchbaseCluster cluster;
    private final Bucket bucket;
    private final LoggingAdapter log;

    public DefaultCouchbase(ExtendedActorSystem system) {
        this.log = Logging.getLogger(system, getClass().getName());
        this.environment = DefaultCouchbaseEnvironment.create();
        this.couchbaseConfig = new CouchbaseConfig(system);
        this.cluster = couchbaseConfig.createCluster(environment);
        this.bucket = couchbaseConfig.openBucket(cluster);
    }

    @Override
    public Bucket getBucket() {
        return bucket;
    }

    @Override
    public AsyncBucket getAsyncBucket() {
        return getBucket().async();
    }

    void shutdown() {
        //TODO handle errors properly and add logging
        bucket.close(30, TimeUnit.SECONDS);
        cluster.disconnect(30, TimeUnit.SECONDS);
        environment.shutdown(30, TimeUnit.SECONDS);
    }
}