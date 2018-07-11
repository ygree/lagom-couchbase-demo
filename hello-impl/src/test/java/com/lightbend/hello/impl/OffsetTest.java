package com.lightbend.hello.impl;

import com.lightbend.lagom.javadsl.persistence.Offset;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.UUID;

public class OffsetTest {

    @Ignore
    @Test
    public void test() {
        String uuid = "e453e4f0-83b6-11e8-a28a-71aa7db99f5d";
        UUID uid = UUID.fromString(uuid);
        Offset.TimeBasedUUID timeBasedUUID = new Offset.TimeBasedUUID(uid);

        Assert.assertEquals("", timeBasedUUID);
    }
}
