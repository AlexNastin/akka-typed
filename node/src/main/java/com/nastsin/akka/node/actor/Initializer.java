package com.nastsin.akka.node.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;
import akka.cluster.sharding.typed.ShardingEnvelope;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.Entity;
import akka.cluster.sharding.typed.javadsl.EntityRef;
import akka.persistence.typed.PersistenceId;
import com.google.protobuf.GeneratedMessageV3;
import com.nastsin.akka.common.entity.AddCommand;
import com.nastsin.akka.common.sharding.Sharding;
import com.nastsin.akka.node.actor.pool.Worker;
import com.nastsin.akka.node.actor.sharding.Balance;
import com.nastsin.akka.node.actor.timer.BatchActor;
import com.nastsin.akka.node.actor.timer.Buncher;

import java.time.Duration;

public class Initializer extends AbstractBehavior<String> {

    private final ClusterSharding sharding = ClusterSharding.get(getContext().getSystem());

    public Initializer(ActorContext<String> context) {
        super(context);
    }

    public static Behavior<String> create() {
        return Behaviors.setup(Initializer::new);
    }

    @Override
    public Receive<String> createReceive() {
        return newReceiveBuilder()
                .onMessageEquals("CustomTimerAkka", () -> {
                    System.out.println("START CustomTimerAkka!");

                    System.out.println("END CustomTimerAkka!");
                    return Behaviors.same();
                })
                .onMessageEquals("TimerAkka", () -> {
                    System.out.println("START TimerAkka!");
                    ActorRef<Buncher.Batch> batchActor = getContext().spawn(Behaviors.setup(BatchActor::new), "batchActor");
                    ActorRef<Buncher.Command> buncher = getContext().spawn(Buncher.create(batchActor, Duration.ofSeconds(3), 10), "buncher");
                    buncher.tell(new Buncher.ExcitingMessage("Init", 0));
                    System.out.println("END TimerAkka!");
                    return Behaviors.same();
                })
                .onMessageEquals("PersistSharding", () -> {
                    System.out.println("START PersistSharding!");
                    ActorRef<ShardingEnvelope<GeneratedMessageV3>> shardRegion = sharding.init(
                            Entity.of(
                                    Sharding.Key.SUBSCRIBER_ENTITY_TYPE,
                                    entityContext ->
                                            Balance.create(
                                                    entityContext.getEntityId(),
                                                    PersistenceId.of(
                                                            entityContext.getEntityTypeKey().name(), entityContext.getEntityId())))
                                    .withRole(Sharding.Role.SUBSCRIBER_SHARD));

                    EntityRef<GeneratedMessageV3> entityRef =
                            sharding.entityRefFor(Sharding.Key.SUBSCRIBER_ENTITY_TYPE, "1");
                    entityRef.tell(AddCommand.getDefaultInstance());

                    shardRegion.tell(new ShardingEnvelope<>("1", AddCommand.getDefaultInstance()));

                    System.out.println("END PersistSharding!");
                    return Behaviors.same();
                })
                .onMessageEquals("PoolReceptionist", () -> {
                    System.out.println("START PoolReceptionist!");
                    //todo: not working
                    ServiceKey<GeneratedMessageV3> serviceKey = ServiceKey.create(GeneratedMessageV3.class, "key-worker");
                    ActorRef<GeneratedMessageV3> worker = getContext().spawn(Worker.create(), "worker");

                    getContext().getSystem().receptionist().tell(Receptionist.register(serviceKey, worker));

                    GroupRouter<GeneratedMessageV3> group = Routers.group(serviceKey);
                    ActorRef<GeneratedMessageV3> router = getContext().spawn(group, "worker-group");

                    for (int i = 0; i < 10; i++) {
                        router.tell(AddCommand.getDefaultInstance());
                    }
                    System.out.println("END PoolReceptionist!");
                    return Behaviors.same();
                })
                .build();
    }
}

