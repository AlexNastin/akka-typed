package com.nastsin.akka.common.entity;

public interface AkkaCommand {

    default String getId() {
        return String.valueOf(hashCode());
    }
}
