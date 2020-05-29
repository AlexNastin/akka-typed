package com.nastsin.akka.node.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import com.google.protobuf.GeneratedMessageV3;

public class Initializer extends AbstractBehavior<Void> {

    private final ClusterSharding sharding = ClusterSharding.get(getContext().getSystem());

//    private final ActorRef<ShardingEnvelope<Message>> shardRegion;

    public Initializer(ActorContext<Void> context) {
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


        ServiceKey<GeneratedMessageV3> serviceKey = ServiceKey.create(GeneratedMessageV3.class, "key-worker");
        ActorRef<GeneratedMessageV3> worker = context.spawn(Worker.create(), "worker");

        context.getSystem().receptionist().tell(Receptionist.register(serviceKey, worker));

        GroupRouter<GeneratedMessageV3> group = Routers.group(serviceKey);
        ActorRef<GeneratedMessageV3> router = context.spawn(group, "worker-group");

        for (int i = 0; i < 10; i++) {
//            router.tell();
        }
    }


    public static Behavior<Void> create() {
        return Behaviors.setup(Initializer::new);
    }

    @Override
    public Receive<Void> createReceive() {
        return newReceiveBuilder()
                .onAnyMessage(any -> {
                    getContext().getLog().info("Any: {}", any);
                    return Behaviors.same();
                })
                .build();
    }
}
