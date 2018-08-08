package com.lightbend.couchbase;

import akka.actor.Extension;
import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.Bucket;

/**
 * Couchbase interface, provides access to the bucket specified in the configuration.
 */
public interface Couchbase extends Extension {

    /**
     * Provides blocking interface to Couchbase Bucket Java API.
     * Prefer {@link #asyncBucket} whenever possible.
     */
    Bucket bucket();

    /**
     * Provides asynchronous non-blocking interface to Couchbase Bucket Java API.
     */
    default AsyncBucket asyncBucket() {
        return bucket().async();
    }
}
