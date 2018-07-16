package com.lightbend.couchbase.blocking;

import akka.actor.Extension;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.CouchbaseCluster;

public interface CouchbaseBlocking extends Extension {
    CouchbaseCluster getCluster();

    Bucket getBucket();
}
