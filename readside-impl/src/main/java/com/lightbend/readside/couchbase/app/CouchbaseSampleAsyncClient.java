package com.lightbend.readside.couchbase.app;

import akka.actor.ActorSystem;
import com.couchbase.client.core.utils.Observables;
import com.couchbase.client.java.*;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.lightbend.readside.couchbase.Couchbase;
import com.lightbend.readside.couchbase.CouchbaseExtension;
import rx.Completable;
import rx.Observable;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

import java.util.concurrent.CountDownLatch;

public class CouchbaseSampleAsyncClient {
    public static void main(String... args) throws Exception {

        ActorSystem actorSystem = ActorSystem.create();
        Couchbase couchbase = CouchbaseExtension.CouchbaseExtensionProvider.get(actorSystem);

        // Initialize the Connection
//        CouchbaseAsyncCluster cluster = CouchbaseAsyncCluster.create("localhost");
//        cluster.authenticate("Administrator", "test123");

//        Observable<AsyncBucket> bucket = cluster.openBucket("test");

        Observable<AsyncBucket> bucket = couchbase.getBucket();

        // Create a JSON Document
        JsonObject arthur = JsonObject.create()
                .put("name", "Arthur123")
                .put("email", "kingarthur@couchbase.com")
                .put("interests", JsonArray.from("Holy Grail", "African Swallows"));

        Observable<JsonDocument> jsonDocumentObservable = bucket.flatMap(b -> b.upsert(JsonDocument.create("u:king_arthur", arthur)));

//        jsonDocumentObservable.subscribe(v -> System.out.println("-----\n" + v));
        final CountDownLatch latch = new CountDownLatch(1);

        jsonDocumentObservable.doOnCompleted(latch::countDown)
                .subscribe(v -> System.out.println("-----\n" + v));

//TODO: use async interface

//        Observable<Integer> just = Observable.just(1, 2, 3);
//        just.subscribe(v -> System.out.println("--+--"));

        latch.await();

        Await.ready(actorSystem.terminate(), Duration.Inf());

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

