package com.nastsin.akka.node.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import com.nastsin.akka.node.actor.time.BatchActor;
import com.nastsin.akka.node.actor.time.Buncher;

import java.time.Duration;

public class Initializer extends AbstractBehavior<String> {

    private final ClusterSharding sharding = ClusterSharding.get(getContext().getSystem());

//    private final ActorRef<ShardingEnvelope<Message>> shardRegion;

    public Initializer(ActorContext<String> context) {
        super(context);

//        this.shardRegion = sharding.init(
//                Entity.of(
//                        Sharding.Key.SUBSCRIBER_ENTITY_TYPE,
//                        entityContext ->
//                                Subscriber.create(
//                                        entityContext.getEntityId(),
//                                        PersistenceId.of(
//                                                entityContext.getEntityTypeKey().name(), entityContext.getEntityId()),
//                                        null))
//                        .withRole(Sharding.Role.SUBSCRIBER_SHARD));

//        RxMessage message = RxMessage.getDefaultInstance();
//        EntityRef<GeneratedMessageV3> entityRef =
//                sharding.entityRefFor(Subscriber.ENTITY_TYPE_KEY, message.getId());
//        entityRef.tell(message);


//        ServiceKey<GeneratedMessageV3> serviceKey = ServiceKey.create(GeneratedMessageV3.class, "key-worker");
//        ActorRef<GeneratedMessageV3> worker = context.spawn(Worker.create(), "worker");
//
//        context.getSystem().receptionist().tell(Receptionist.register(serviceKey, worker));
//
//        GroupRouter<GeneratedMessageV3> group = Routers.group(serviceKey);
//        ActorRef<GeneratedMessageV3> router = context.spawn(group, "worker-group");
//
//        for (int i = 0; i < 10; i++) {
//            router.tell();
//        }
    }


    public static Behavior<String> create() {
        return Behaviors.setup(Initializer::new);
    }

    @Override
    public Receive<String> createReceive() {
        return newReceiveBuilder()
                .onAnyMessage(any -> {
                    getContext().getLog().info("Any: {}", any);
                    System.out.println("START !");

                    ActorRef<Buncher.Batch> batchActor = getContext().spawn(Behaviors.setup(BatchActor::new), "batchActor");

                    ActorRef<Buncher.Command> buncher = getContext().spawn(Buncher.create(batchActor, Duration.ofSeconds(3), 10), "buncher");

                    buncher.tell(new Buncher.ExcitingMessage("Init", 0));

                    System.out.println("END!");

                    return Behaviors.same();
                })
                .build();
    }
}
