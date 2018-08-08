package com.lightbend.hello.impl.readside;

import akka.Done;
import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.ParameterizedN1qlQuery;
import com.lightbend.couchbase.Couchbase;
import com.lightbend.hello.impl.HelloEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.Offset;
import rx.Observable;
import utils.RxJava8Utils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

@Singleton
public class ReadSideRepository {

    private final Couchbase couchbase;

    @Inject
    public ReadSideRepository(Couchbase couchbase) {
        this.couchbase = couchbase;
    }

    public CompletionStage<Offset> getOffset(AggregateEventTag<HelloEvent> tag) {

        AsyncBucket bucket = couchbase.asyncBucket();
        String docId = offsetDocId(tag);

        Observable<Optional<Offset>> result = bucket
                .get(docId).map(v ->
                        Optional.ofNullable(v.content().getString("offset"))
                                //TODO need support long based offset as well
                                .map(UUID::fromString)
                                .map(uid -> ((Offset) new Offset.TimeBasedUUID(uid)))
                );

        return RxJava8Utils.fromSingleOptOptObservable(result).thenApply(v -> v.orElse(Offset.NONE));
    }

    public CompletionStage<Done> updateOffset(AggregateEventTag<HelloEvent> tag, Offset offset) {

        AsyncBucket bucket = couchbase.asyncBucket();

        JsonObject obj = JsonObject.create()
                .put("offset", offset.toString());

        JsonDocument doc = JsonDocument.create(offsetDocId(tag), obj);

        Observable<Done> result = bucket
                .upsert(doc)
                .map(b -> Done.getInstance());

        return RxJava8Utils.fromSingleObservable(result);
    }

    private String offsetDocId(AggregateEventTag<HelloEvent> tag) {
        return "hello_user_messages:tag_offset:" + tag.tag();
    }

    public CompletionStage<Done> updateMessage(String name, String message) {

        AsyncBucket bucket = couchbase.asyncBucket();

        JsonObject obj = JsonObject.create()
                .put("messages", JsonArray.from(message))
                .put("message", message);

        String docId = userMessageDocId(name);
        JsonDocument doc = JsonDocument.create(docId, obj);

        String queryText = "UPDATE test USE KEYS $1 SET messages = ARRAY_PREPEND($2, IFNULL(messages, [])), message = $2;";
        ParameterizedN1qlQuery query = N1qlQuery.parameterized(queryText, JsonArray.from(docId, message));

        Observable<Done> result = bucket
                .insert(doc).map(x -> Done.getInstance())
                .onErrorResumeNext(e -> bucket.query(query).map(x -> Done.getInstance()));

        return RxJava8Utils.fromSingleObservable(result);
    }

    private String userMessageDocId(String name) {
        return "hello_user_messages:" + name;
    }

}

