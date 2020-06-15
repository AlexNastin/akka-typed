package com.nastsin.akka.common.entity;

import akka.actor.typed.ActorRef;
import lombok.Data;

import java.util.List;

@Data
public class Control implements AkkaCommand {
    private int id;
    private String answer;
    private List<Double> timing;
    private ActorRef<AkkaCommand> replayTo;

    public Control(ActorRef<AkkaCommand> replayTo) {
        this.replayTo = replayTo;
    }

    @Override
    public String getId() {
        return String.valueOf(id);
    }
}
