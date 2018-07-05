package com.lightbend.couchbase.sampleapp;

import akka.actor.ActorSystem;
import com.couchbase.client.core.utils.Blocking;
import com.couchbase.client.java.*;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.lightbend.couchbase.Couchbase;
import com.lightbend.couchbase.CouchbaseExtension;
import rx.Observable;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CouchbaseSampleAsyncClient {
    public static void main(String... args) throws Exception {

        ActorSystem actorSystem = ActorSystem.create();

        // use Couchbase Akka Extension to manage Couchbase resources
        Couchbase couchbase = CouchbaseExtension.CouchbaseExtensionProvider.get(actorSystem);

        Observable<AsyncBucket> bucket = couchbase.getBucket();

        // Create a JSON Document
        JsonObject arthur = JsonObject.create()
                .put("name", "Arthur-4444")
                .put("email", "kingarthur@couchbase.com")
                .put("interests", JsonArray.from("Holy Grail", "African Swallows"));

        Observable<JsonDocument> jsonDocumentObservable = bucket.flatMap(b -> b.upsert(JsonDocument.create("u:king_arthur", arthur)));

        Blocking.blockForSingle(jsonDocumentObservable, 30, TimeUnit.SECONDS);

        Await.ready(actorSystem.terminate(), Duration.Inf());

//TODO: use async interface

//        // Store the Document
//        bucket.upsert(JsonDocument.create("u:king_arthur", arthur));
//
//        // Load the Document and print it
//        // Prints Content and Metadata of the stored Document
//        System.out.println(bucket.get("u:king_arthur"));
//
//        // Create a N1QL Primary Index (but ignore if it exists)
//        bucket.bucketManager().createN1qlPrimaryIndex(true, false);
//
//        // Perform a N1QL Query
//        N1qlQueryResult result = bucket.query(
//                N1qlQuery.parameterized("SELECT name FROM `test` WHERE $1 IN interests",
//                        JsonArray.from("African Swallows"))
//        );
//
//        // Print each found Row
//        for (N1qlQueryRow row : result) {
//            // Prints {"name":"Arthur"}
//            System.out.println(row);
//        }
    }
}

