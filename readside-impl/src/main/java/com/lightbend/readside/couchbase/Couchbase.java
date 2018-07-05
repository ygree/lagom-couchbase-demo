package com.lightbend.readside.couchbase;

import akka.actor.Extension;
import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import rx.Observable;

public interface Couchbase extends Extension {
    CouchbaseEnvironment getEnvironment();

    Observable<AsyncBucket> getBucket();
}
