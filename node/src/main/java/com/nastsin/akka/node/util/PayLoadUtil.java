package com.nastsin.akka.node.util;

import akka.actor.typed.ActorRef;
import com.nastsin.akka.common.entity.AkkaCommand;
import com.nastsin.akka.common.entity.Do;
import com.nastsin.akka.common.entity.Result;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PayLoadUtil {

    public static <T> void startTimerTest(int period, ActorRef<T> actorRef, T command, long timeOfWork, ActorRef<AkkaCommand> analyser, int poolSize) {
        ScheduledFuture<?> scheduledFuture = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() ->
                actorRef.tell(command), 0, period, TimeUnit.MICROSECONDS);

        TimerTask task = new TimerTask() {
            public void run() {
                scheduledFuture.cancel(true);
                try {
                    Thread.sleep(10000);
                    analyser.tell(new Do(poolSize));
                    Thread.sleep(5000);
                    analyser.tell(new Result());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, timeOfWork);
    }
}
