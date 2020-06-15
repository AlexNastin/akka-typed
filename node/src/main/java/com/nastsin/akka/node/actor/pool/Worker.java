package com.nastsin.akka.node.actor.pool;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.nastsin.akka.common.entity.AkkaCommand;
import com.nastsin.akka.common.entity.PoolDo;

public class Worker extends AbstractBehavior<AkkaCommand> {

    public static Behavior<AkkaCommand> create() {
        return Behaviors.setup(Worker::new);
    }

    public Worker(ActorContext<AkkaCommand> context) {
        super(context);
    }

    @Override
    public Receive<AkkaCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(PoolDo.class, param -> {
                    getContext().getLog().info("Obj: {}, path: {}", param, getContext().getSelf().path().toString());
                    return Behaviors.same();
                })
                .onAnyMessage(param -> {
                    getContext().getLog().info("Worker-Any: {}", param);
                    return Behaviors.same();
                })
                .build();
    }
}