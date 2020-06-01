package com.nastsin.akka.node.actor.timer.custom;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import com.nastsin.akka.common.entity.AkkaCommand;
import com.nastsin.akka.common.entity.Control;
import com.nastsin.akka.common.entity.Do;
import com.nastsin.akka.common.entity.Timeout;

import java.time.Duration;

public class TimerActor extends AbstractBehavior<AkkaCommand> {

    private final String TIMER_KEY = "TimeoutKey";

    private final TimerScheduler<AkkaCommand> timer;
    private final Duration after;

    private int countCommand;
    private int countTimeout;

    public TimerActor(ActorContext<AkkaCommand> context, TimerScheduler<AkkaCommand> timer, Duration after) {
        super(context);
        this.timer = timer;
        this.after = after;
    }

    public static Behavior<AkkaCommand> create(Duration after) {
        return Behaviors.withTimers(timer -> Behaviors.setup(param -> new TimerActor(param, timer, after)));
    }

    @Override
    public Receive<AkkaCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(Do.class, param -> {
                    getContext().getLog().info("Do!");
                    countCommand++;
                    timer.startSingleTimer(new Timeout(System.nanoTime()), after);
                    return Behaviors.same();
                })
                .onMessage(Timeout.class, timeout -> {
                    countTimeout++;
                    getContext().getLog().info("Timeout. DurationSec: {}, TimeSec: {}, countCommand: {}, countTimeout: {}",
                            after.getSeconds(), (double) (System.nanoTime() - timeout.timestamp) / 1000000000, countCommand, countTimeout);
                    return Behaviors.same();
                })
                .onMessage(Control.class, control -> {
                    control.setAnswer("countCommand: " + countCommand + " , countTimeout: " + countTimeout);
                    control.getReplayTo().tell(control);
                    return Behaviors.same();
                })
                .build();
    }
}
