package com.nastsin.akka.common.entity;

import lombok.Data;

@Data
public class Do implements AkkaCommand {
    private int poolSize;

    public Do(int poolSize) {
        this.poolSize = poolSize;
    }

    public Do() {
    }

    @Override
    public String getId() {
        return String.valueOf(hashCode());
    }
}
