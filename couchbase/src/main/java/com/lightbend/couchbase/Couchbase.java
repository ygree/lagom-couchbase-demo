package com.lightbend.couchbase;

import akka.actor.Extension;
import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.Bucket;

public interface Couchbase extends Extension {

    Bucket getBucket();

    default AsyncBucket getAsyncBucket() {
        return getBucket().async();
    }
}
