package com.nastsin.akka.common.entity;

public class Timeout implements AkkaCommand {
    public long timestamp;

    public Timeout(long timestamp) {
        this.timestamp = timestamp;
    }
}