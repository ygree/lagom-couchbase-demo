package com.lightbend.couchbase;

import akka.actor.Extension;
import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.env.CouchbaseEnvironment;

public interface Couchbase extends Extension {
    CouchbaseEnvironment getEnvironment();

    AsyncBucket getBucket();
}
