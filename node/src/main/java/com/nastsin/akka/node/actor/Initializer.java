package com.nastsin.akka.node.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.*;
import akka.actor.typed.receptionist.Receptionist;
import akka.cluster.sharding.typed.ShardingEnvelope;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.Entity;
import akka.cluster.sharding.typed.javadsl.EntityRef;
import akka.persistence.typed.PersistenceId;
import com.google.protobuf.GeneratedMessageV3;
import com.nastsin.akka.common.entity.AddCommand;
import com.nastsin.akka.common.entity.AkkaCommand;
import com.nastsin.akka.common.entity.Do;
import com.nastsin.akka.common.entity.PoolDo;
import com.nastsin.akka.common.entity.init.*;
import com.nastsin.akka.common.sharding.Sharding;
import com.nastsin.akka.node.actor.pool.Worker;
import com.nastsin.akka.node.actor.sharding.Balance;
import com.nastsin.akka.node.actor.timer.BatchActor;
import com.nastsin.akka.node.actor.timer.Buncher;
import com.nastsin.akka.node.actor.timer.custom.Analyser;
import com.nastsin.akka.node.actor.timer.custom.TimerActor;
import com.nastsin.akka.node.util.PayLoadUtil;

import java.time.Duration;

import static com.nastsin.akka.common.util.Key.SERVICE_KEY;

public class Initializer extends AbstractBehavior<InitCommand> {

    private final ClusterSharding sharding = ClusterSharding.get(getContext().getSystem());

    public Initializer(ActorContext<InitCommand> context) {
        super(context);
    }

    public static Behavior<InitCommand> create() {
        return Behaviors.setup(Initializer::new);
    }

    @Override
    public Receive<InitCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(TimerCaseInit.class, param -> {
                    System.out.println("START CustomTimerAkka!");

                    PoolRouter<AkkaCommand> pool =
                            Routers.pool(param.getPoolSize(), Behaviors.supervise(
                                    TimerActor.create(param.getDuration())).onFailure(SupervisorStrategy.restart())).withRoundRobinRouting();

                    ActorRef<AkkaCommand> router = getContext().spawn(pool, "timer-pool");

                    ActorRef<AkkaCommand> analyser = getContext().spawn(Analyser.create(router), "analyser");

                    PayLoadUtil.startTimerTest(param.getPeriod(), router, new Do(), param.getTimeOfWork(), analyser, param.getPoolSize());

                    System.out.println("END CustomTimerAkka!");
                    return Behaviors.same();
                })
                .onMessage(TimerAkkaCaseInit.class, param -> {
                    System.out.println("START TimerAkka!");
                    ActorRef<Buncher.Batch> batchActor = getContext().spawn(Behaviors.setup(BatchActor::new), "batchActor");
                    ActorRef<Buncher.Command> buncher = getContext().spawn(Buncher.create(batchActor, Duration.ofSeconds(3), 10), "buncher");
                    buncher.tell(new Buncher.ExcitingMessage("Init", 0));
                    System.out.println("END TimerAkka!");
                    return Behaviors.same();
                })
                .onMessage(PersistShardingCaseInit.class, param -> {
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
                .onMessage(PoolReceptionistCaseInit.class, param -> {
                    System.out.println("START PoolReceptionist!");

                    PoolRouter<AkkaCommand> pool =
                            Routers.pool(100, Behaviors.supervise(Worker.create()).onFailure(SupervisorStrategy.restart()))
                                    .withConsistentHashingRouting(2,
                                            AkkaCommand::getId);

                    ActorRef<AkkaCommand> worker = getContext().spawn(pool, "worker");

                    ActorRef<Receptionist.Command> receptionist = getContext().getSystem().receptionist();

                    receptionist.tell(Receptionist.register(SERVICE_KEY, worker));

                    GroupRouter<AkkaCommand> group = Routers.group(SERVICE_KEY);

                    ActorRef<AkkaCommand> router = getContext().spawn(group, "worker-group");

                    getContext().getLog().info(router.path().toString());

                    worker.tell(new PoolDo(1));
                    worker.tell(new PoolDo(2));
                    worker.tell(new PoolDo(4));
                    worker.tell(new PoolDo(5));
                    worker.tell(new PoolDo(6));

                    Thread.sleep(5000);

                    for (int i = 0; i < 1000; i++) {
                        router.tell(new PoolDo(i));
                        Thread.sleep(5);
                    }
                    System.out.println("END PoolReceptionist!");
                    return Behaviors.same();
                })
                .build();
    }
}

