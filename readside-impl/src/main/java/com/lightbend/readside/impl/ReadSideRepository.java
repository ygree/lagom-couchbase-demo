package com.lightbend.readside.impl;

import akka.Done;
import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.lightbend.couchbase.Couchbase;
import rx.Observable;
import utils.RxJava8Utils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@Singleton
public class ReadSideRepository {

    private final Couchbase couchbase;

    @Inject
    public ReadSideRepository(Couchbase couchbase) {
        this.couchbase = couchbase;
    }

    public CompletionStage<Done> updateMessage(String name, String message) {

        Observable<AsyncBucket> bucket = couchbase.getBucket();

        JsonObject obj = JsonObject.create()
                .put("message", message);

        JsonDocument doc = JsonDocument.create(userMessageDocId(name), obj);

        //TODO: implement append message logic if it hasn't changed
        Observable<JsonDocument> result = bucket.flatMap(b -> b.upsert(doc));

        return RxJava8Utils.fromSingleObservable(result.map(v -> Done.getInstance()));
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
    }
}

