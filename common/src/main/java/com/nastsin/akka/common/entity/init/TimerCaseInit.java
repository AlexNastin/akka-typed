package com.nastsin.akka.common.entity.init;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;

@Data
@AllArgsConstructor
public class TimerCaseInit implements InitCommand {
    private int poolSize;
    private Duration duration;
    private int period;
    private long timeOfWork;
}
