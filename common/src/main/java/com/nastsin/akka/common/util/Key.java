package com.nastsin.akka.common.util;

import akka.actor.typed.receptionist.ServiceKey;
import com.nastsin.akka.common.entity.AkkaCommand;

public class Key {
    public static final ServiceKey<AkkaCommand> SERVICE_KEY = ServiceKey.create(AkkaCommand.class, "key-worker");
}
