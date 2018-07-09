package com.lightbend.readside.impl;

import akka.Done;
import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.ParameterizedN1qlQuery;
import com.couchbase.client.java.query.dsl.Expression;
import com.couchbase.client.java.query.dsl.functions.ConditionalFunctions;
import com.couchbase.client.java.query.dsl.path.UpdateSetPath;
import com.lightbend.couchbase.Couchbase;
import rx.Observable;
import utils.RxJava8Utils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static com.couchbase.client.java.query.Update.update;
import static com.couchbase.client.java.query.dsl.Expression.*;
import static com.couchbase.client.java.query.dsl.functions.ArrayFunctions.arrayIfNull;
import static com.couchbase.client.java.query.dsl.functions.ArrayFunctions.arrayPrepend;
import static com.couchbase.client.java.query.dsl.functions.ConditionalFunctions.*;

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
                .put("messages", JsonArray.empty());

        String docId = userMessageDocId(name);
        JsonDocument doc = JsonDocument.create(docId, obj);

        String queryText = "UPDATE test USE KEYS $1 SET messages = ARRAY_PREPEND($2, IFNULL(messages, [])), message = $2;";
        ParameterizedN1qlQuery query = N1qlQuery.parameterized(queryText, JsonArray.from(docId, message));

/* TODO: it doesn't translates into a correct query
//TODO: see com.lightbend.readside.impl.N1qlDslTests
        UpdateSetPath updateSetPath =
                update("test")
                        .useKeys(docId)
                        .set("message", message)
                        .set("messages", arrayPrepend(x(message), ifNull(x("messages"), x(JsonArray.empty()))));
*/

        Observable<Done> result = bucket
                .flatMap(b -> b.insert(doc).map(x -> b))
                .onExceptionResumeNext(bucket)
                .flatMap(b -> b.query(query))
//                .flatMap(b -> b.query(updateSetPath))
                .map(v -> Done.getInstance());

        return RxJava8Utils.fromSingleObservable(result);
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

