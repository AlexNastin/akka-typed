package com.nastsin.akka.common.entity;

import akka.actor.typed.ActorRef;
import lombok.Data;

@Data
public class Control implements AkkaCommand {
    private int id;
    private String answer;
    private ActorRef<AkkaCommand> replayTo;

    public Control(ActorRef<AkkaCommand> replayTo) {
        this.replayTo = replayTo;
    }
}
