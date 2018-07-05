package com.lightbend.readside.impl;

import akka.Done;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import akka.actor.ActorSystem;
import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQuery;
import com.lightbend.couchbase.Couchbase;
import com.lightbend.couchbase.CouchbaseExtension;
import rx.Observable;
import utils.RxJava8Utils;

@Singleton
public class ReadSideRepository {

//    private final Map<String, String> greetings;

    private final Couchbase couchbase;

    @Inject
    public ReadSideRepository(ActorSystem system) {
//        greetings = new HashMap<>();

        couchbase = CouchbaseExtension.CouchbaseExtensionProvider.get(system);
    }

    public CompletionStage<Done> updateMessage(String name, String message) {

        Observable<AsyncBucket> bucket = couchbase.getBucket();

        JsonObject obj = JsonObject.create()
                .put("message", message);

        JsonDocument doc = JsonDocument.create(userMessageDocId(name), obj);

        //TODO: implement append message logic if it hasn't changed
        Observable<JsonDocument> result = bucket.flatMap(b -> b.upsert(doc));

        return RxJava8Utils.fromSingleObservable(result.map(v -> Done.getInstance()));
//        greetings.put(name, message);
    }

    private String userMessageDocId(String name) {
        return "user_messages:" + name;
    }

    public CompletionStage<Optional<String>> getMessage(String name) {

        String docId = userMessageDocId(name);

        Observable<AsyncBucket> bucket = couchbase.getBucket();

        Observable<Optional<String>> result = bucket.flatMap(b -> b.get(docId))
                .map(v -> Optional.ofNullable(v.content().getString("message")));

        return RxJava8Utils.fromSingleOptOptObservable(result);
//        return CompletableFuture.completedFuture(Optional.ofNullable(greetings.get(name)));
    }
}

