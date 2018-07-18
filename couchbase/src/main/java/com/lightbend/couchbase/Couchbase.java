package com.lightbend.couchbase;

import akka.actor.Extension;
import com.couchbase.client.java.AsyncBucket;

public interface Couchbase extends Extension {

    AsyncBucket getBucket();
}
