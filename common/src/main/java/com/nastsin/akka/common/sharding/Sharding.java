package com.nastsin.akka.common.sharding;

import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import com.google.protobuf.GeneratedMessageV3;

public final class Sharding {

    private Sharding() {
    }

    public static class Key {
        public static final EntityTypeKey<GeneratedMessageV3> SUBSCRIBER_ENTITY_TYPE =
                EntityTypeKey.create(GeneratedMessageV3.class, "subscriberRegion");
    }

    public static class Role {
        public static final String SUBSCRIBER_SHARD = "node";
    }
}