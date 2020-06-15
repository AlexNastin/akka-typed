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
import com.nastsin.akka.common.entity.Result;
import org.apache.commons.math3.stat.StatUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Analyser extends AbstractBehavior<AkkaCommand> {

    private final ActorRef<AkkaCommand> router;

    private final List<Double> timing = new ArrayList<>(1000000);

    private final List<Control> results = new ArrayList<>();

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
                    for (int i = 0; i < param.getPoolSize(); i++) {
                        router.tell(new Control(getContext().getSelf()));
                    }
                    return Behaviors.same();
                })
                .onMessage(Control.class, control -> {
                    results.add(control);
                    timing.addAll(control.getTiming());
                    getContext().getLog().debug("Control: {}", control.getAnswer());
                    return Behaviors.same();
                })
                .onMessage(Result.class, param -> {
                    List<Control> collect = results.stream().sorted(Comparator.comparingInt(value -> Integer.parseInt(value.getId())))
                            .collect(Collectors.toList());
                    collect.forEach(control -> getContext().getLog().error("Control: {}", control.getAnswer()));
                    double[] timingArray = new double[timing.size()];

                    for (int i = 0; i < timing.size(); i++) {
                        timingArray[i] = timing.get(i);
                    }

                    double average = timing.stream().mapToDouble(value -> value).average().orElse(Double.NaN);
                    double max = timing.stream().mapToDouble(value -> value).max().orElse(Double.NaN);
                    double min = timing.stream().mapToDouble(value -> value).min().orElse(Double.NaN);
                    double p50 = StatUtils.percentile(timingArray, 50.0);
                    double p95 = StatUtils.percentile(timingArray, 95.0);
                    double p99 = StatUtils.percentile(timingArray, 99.0);

                    getContext().getLog().error("Average Time Millis: {} ,Max: {} ,Min: {} ,Size: {}", average, max, min, timing.size());
                    getContext().getLog().error("P-50: {} , P-95: {} , P-99: {}", p50, p95, p99);
                    return Behaviors.same();
                })
                .build();
    }
}
