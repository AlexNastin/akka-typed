package com.nastsin.akka.node.util;

import akka.actor.typed.ActorRef;
import com.nastsin.akka.common.entity.AkkaCommand;
import com.nastsin.akka.common.entity.Do;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PayLoadUtil {

    public static <T> void startTest(int period, ActorRef<T> actorRef, T command, long timeOfWork, ActorRef<AkkaCommand> analyser) {
        ScheduledFuture<?> scheduledFuture = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() ->
                actorRef.tell(command), 0, period, TimeUnit.MICROSECONDS);

        TimerTask task = new TimerTask() {
            public void run() {
                scheduledFuture.cancel(true);
                try {
                    Thread.sleep(10000);
                    analyser.tell(new Do(500));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, timeOfWork);
    }
}
