package com.lightbend.readside.impl;

import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.query.dsl.path.UpdateSetPath;
import org.junit.Assert;
import org.junit.Test;

import static com.couchbase.client.java.query.Update.update;
import static com.couchbase.client.java.query.dsl.Expression.x;
import static com.couchbase.client.java.query.dsl.functions.ArrayFunctions.arrayPrepend;
import static com.couchbase.client.java.query.dsl.functions.ConditionalFunctions.ifNull;

public class N1qlDslTests {

    @Test
    public void test() {

        UpdateSetPath updateSetPath =
                update("test")
                        .useKeys("$1")
                        .set("messages", arrayPrepend(ifNull(x("messages"), x(JsonArray.empty())), x("$2")))
                        .set("message", "$2");


        Assert.assertEquals("UPDATE test USE KEYS $1 SET messages = ARRAY_PREPEND($2, IFNULL(messages, [])), message = $2;", updateSetPath.toString());

    }
}
