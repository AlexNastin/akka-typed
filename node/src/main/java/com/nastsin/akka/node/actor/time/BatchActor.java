package com.nastsin.akka.node.actor.time;

import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class BatchActor extends AbstractBehavior<Buncher.Batch> {

    public BatchActor(ActorContext<Buncher.Batch> context) {
        super(context);

    }

    @Override
    public Receive<Buncher.Batch> createReceive() {
        return newReceiveBuilder()
                .onMessage(Buncher.Batch.class, message -> {
                    getContext().getLog().info("-----Timeout-------");
                    getContext().getLog().info("BatchActor: {}", message.getMessages());
                    return Behaviors.same();
                })
                .build();
    }
}