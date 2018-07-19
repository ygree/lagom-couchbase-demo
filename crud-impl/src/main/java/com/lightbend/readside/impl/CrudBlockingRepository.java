package com.lightbend.readside.impl;

import akka.Done;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.error.DocumentAlreadyExistsException;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.ParameterizedN1qlQuery;
import com.lightbend.couchbase.Couchbase;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Singleton
public class CrudBlockingRepository implements CrudRepository {

    private final Couchbase couchbase;

    @Inject
    public CrudBlockingRepository(Couchbase couchbase) {
        this.couchbase = couchbase;
    }

    private String userMessageDocId(String name) {
        return "crud_user_messages:" + name;
    }

    public CompletionStage<Done> updateMessage(String name, String message) {

        Bucket bucket = couchbase.getBucket();

        JsonObject obj = JsonObject.create()
                .put("messages", JsonArray.from(message))
                .put("message", message);

        String docId = userMessageDocId(name);
        JsonDocument doc = JsonDocument.create(docId, obj);

        String queryText = "UPDATE test USE KEYS $1 SET messages = ARRAY_PREPEND($2, IFNULL(messages, [])), message = $2;";
        ParameterizedN1qlQuery query = N1qlQuery.parameterized(queryText, JsonArray.from(docId, message));

        return CompletableFuture.supplyAsync(() -> {
            try {
                bucket.insert(doc);
            } catch (DocumentAlreadyExistsException e) {
                bucket.query(query);
            }
            return Done.getInstance();
        });
    }

    public CompletionStage<Optional<String>> getMessage(String name) {

        String docId = userMessageDocId(name);

        Bucket bucket = couchbase.getBucket();


        JsonDocument jsonDocument = bucket.get(docId);

        Optional<String> result = Optional.ofNullable(jsonDocument).map(d -> d.content().getString("message"));

        return CompletableFuture.completedFuture(result);
    }
}

