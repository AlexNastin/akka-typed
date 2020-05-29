package com.nastsin.akka.common.entity;

import lombok.Data;

@Data
public class Do implements AkkaCommand {
    private int value;

    public Do(int value) {
        this.value = value;
    }

    public Do() {
    }
}
