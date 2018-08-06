package com.lightbend.couchbase.sampleapp;

import akka.actor.ActorSystem;
import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.AsyncN1qlQueryResult;
import com.couchbase.client.java.query.N1qlQuery;
import com.lightbend.couchbase.Couchbase;
import com.lightbend.couchbase.CouchbaseExtension;
import rx.Observable;

public class CouchbaseSampleAsyncClient {
    public static void main(String... args) {

        ActorSystem actorSystem = ActorSystem.create();

        // use Couchbase Akka Extension to manage Couchbase resources
        Couchbase couchbase = CouchbaseExtension.CouchbaseExtensionProvider.get(actorSystem);

        AsyncBucket bucket = couchbase.getAsyncBucket();

        // Create a JSON Document
        JsonObject arthur = JsonObject.create()
                .put("name", "Arthur-4444")
                .put("email", "kingarthur@couchbase.com")
                .put("interests", JsonArray.from("Holy Grail", "African Swallows"));

        // Store the Document
        JsonDocument doc = JsonDocument.create("u:king_arthur", arthur);
        Observable<JsonDocument> storeDoc = bucket.upsert(doc);

        // Load the Document and print it
        Observable<JsonDocument> readDoc = storeDoc.concatMap(ignore -> bucket.get("u:king_arthur"));

        // Prints Content and Metadata of the stored Document
        readDoc.subscribe(System.out::println);

        // Create a N1QL Primary Index (but ignore if it exists)
        Observable<Boolean> createIndex = readDoc.concatMap(ignore -> bucket.bucketManager()
                .concatMap(bm -> bm.createN1qlPrimaryIndex(true, false)));

        // Perform a N1QL Query
        Observable<AsyncN1qlQueryResult> result = createIndex.concatMap(ignore -> bucket.query(
            N1qlQuery.parameterized("SELECT name FROM `test` WHERE $1 IN interests",
                    JsonArray.from("African Swallows"))
        ));

        // Print each found Row
        result.concatMap(rs -> rs.rows())
        .doOnCompleted(actorSystem::terminate)
        .subscribe(System.out::println);
    }
}

