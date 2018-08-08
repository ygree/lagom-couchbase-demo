package com.lightbend.couchbase.sampleapp;

import com.couchbase.client.core.utils.Blocking;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.lightbend.couchbase.test.CouchbaseMockTest;
import org.junit.Test;
import rx.Observable;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class CouchbaseSampleTest extends CouchbaseMockTest {

    @Test
    public void test() {
        JsonObject arthur = JsonObject.create()
                .put("name", "Arthur-4444")
                .put("email", "kingarthur@couchbase.com")
                .put("interests", JsonArray.from("Holy Grail", "African Swallows"));

        String docId = "u:king_arthur";
        JsonDocument doc = JsonDocument.create(docId, arthur);
        Observable<JsonDocument> jsonDocumentObservable = getCouchbase().asyncBucket().upsert(doc);
        Blocking.blockForSingle(jsonDocumentObservable, 30, TimeUnit.SECONDS);

        JsonObject readDoc = getCouchbase().asyncBucket().get(docId).toBlocking().first().content();

        assertEquals("Arthur-4444", readDoc.getString("name"));
    }
}
