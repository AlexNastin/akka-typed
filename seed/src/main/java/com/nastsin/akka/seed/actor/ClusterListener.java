package com.nastsin.akka.seed.actor;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.cluster.ClusterEvent;
import akka.cluster.typed.Cluster;
import akka.cluster.typed.Subscribe;

public class ClusterListener extends AbstractBehavior<ClusterEvent.ClusterDomainEvent> {

    private final Cluster cluster = Cluster.get(getContext().getSystem());

    public static Behavior<ClusterEvent.ClusterDomainEvent> create() {
        return Behaviors.setup(ClusterListener::new);
    }

    public ClusterListener(ActorContext<ClusterEvent.ClusterDomainEvent> context) {
        super(context);
        cluster.subscriptions().tell(Subscribe.create(getContext().getSelf(), ClusterEvent.ClusterDomainEvent.class));
    }

    @Override
    public Receive<ClusterEvent.ClusterDomainEvent> createReceive() {
        return newReceiveBuilder()
                .onMessage(ClusterEvent.MemberUp.class, memberUp -> {
                    getContext().getLog().info("Node is Up: {}", memberUp.member());
                    return Behaviors.same();
                })
                .onAnyMessage(clusterEvent -> {
                    getContext().getLog().info("Cluster Event: {}", clusterEvent);
                    return Behaviors.same();
                })
                .build();
    }
}