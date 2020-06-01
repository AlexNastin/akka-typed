package com.nastsin.akka.node.actor.timer.custom;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.nastsin.akka.common.entity.AkkaCommand;
import com.nastsin.akka.common.entity.Control;
import com.nastsin.akka.common.entity.Do;

public class Analyser extends AbstractBehavior<AkkaCommand> {

    private final ActorRef<AkkaCommand> router;

    public Analyser(ActorContext<AkkaCommand> context, ActorRef<AkkaCommand> router) {
        super(context);
        this.router = router;
    }

    public static Behavior<AkkaCommand> create(ActorRef<AkkaCommand> router) {
        return Behaviors.setup(context -> new Analyser(context, router));
    }

    @Override
    public Receive<AkkaCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(Do.class, param -> {
                    for (int i = 0; i < param.getValue(); i++) {
                        router.tell(new Control(getContext().getSelf()));
                    }
                    return Behaviors.same();
                })
                .onMessage(Control.class, param -> {
                    getContext().getLog().error("Control: {}", param.getAnswer());
                    return Behaviors.same();
                })
                .build();
    }
}
