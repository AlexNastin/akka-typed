package com.nastsin.akka.node.actor.sharding;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.cluster.sharding.typed.ShardingEnvelope;
import akka.persistence.typed.PersistenceId;
import akka.persistence.typed.javadsl.*;
import com.google.protobuf.GeneratedMessageV3;
import com.nastsin.akka.common.entity.AddCommand;
import com.nastsin.akka.common.entity.AddEvent;
import com.nastsin.akka.common.entity.BalanceState;

public class Balance extends EventSourcedBehavior<GeneratedMessageV3, GeneratedMessageV3, BalanceState> {

    private final ActorContext<GeneratedMessageV3> context;

    private String entityId;

    public Balance(ActorContext<GeneratedMessageV3> context, String entityId, PersistenceId persistenceId) {
        super(persistenceId);
        this.context = context;
        this.entityId = entityId;
        this.context.getLog().info("Starting Balance {}", entityId);
    }

    public static Behavior<GeneratedMessageV3> create(String entityId, PersistenceId persistenceId) {
        return Behaviors.setup(context -> new Balance(context, entityId, persistenceId));
    }

    @Override
    public RetentionCriteria retentionCriteria() {
        return RetentionCriteria.snapshotEvery(1, 1);
    }

    @Override
    public BalanceState emptyState() {
        return BalanceState.getDefaultInstance();
    }

    @Override
    public CommandHandler<GeneratedMessageV3, GeneratedMessageV3, BalanceState> commandHandler() {
        return newCommandHandlerBuilder()
                .forAnyState()
                .onCommand(AddCommand.class, this::onAddCommand)
                .build();
    }

    private Effect<GeneratedMessageV3, BalanceState> onAddCommand(BalanceState state, AddCommand command) {
        AddEvent addEvent = AddEvent.newBuilder().setId(command.getId()).build();
        return Effect().persist(addEvent);
    }

    @Override
    public EventHandler<BalanceState, GeneratedMessageV3> eventHandler() {
        return newEventHandlerBuilder()
                .forAnyState()
                .onEvent(AddEvent.class, (state, event) -> {
                    //working
                    return state;
                })
                .build();
    }
}
