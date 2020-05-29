package com.nastsin.akka.node.actor;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.google.protobuf.GeneratedMessageV3;

public class Worker extends AbstractBehavior<GeneratedMessageV3> {

    public static Behavior<GeneratedMessageV3> create() {
        return Behaviors.setup(Worker::new);
    }

    public Worker(ActorContext<GeneratedMessageV3> context) {
        super(context);
    }

    @Override
    public Receive<GeneratedMessageV3> createReceive() {
        return newReceiveBuilder()
                .onMessage(GeneratedMessageV3.class, param -> {
                    getContext().getLog().info("AAAAAA: {}", param);
                    return Behaviors.same();
                })
                .onAnyMessage(param -> {
                    getContext().getLog().info("DDDDDD: {}", param);
                    return Behaviors.same();
                })
                .build();
    }
}