package com.nastsin.akka.node;

import akka.actor.typed.ActorSystem;
import com.nastsin.akka.common.entity.init.InitCommand;
import com.nastsin.akka.common.entity.init.TimerCaseInit;
import com.nastsin.akka.node.actor.Initializer;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class CoreNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoreNode.class);

    public static void main(String[] args) {
        List<String> argsList = Arrays.asList(args);
        argsList.forEach(System.out::println);

        ActorSystem<InitCommand> actorSystem = ActorSystem.create(Initializer.create(), "akka-test-system", loadConfig());

//        actorSystem.tell("TimerAkka");
        actorSystem.tell(new TimerCaseInit(Integer.parseInt(args[0]), Duration.ofMillis(Long.parseLong(args[1])), Integer.parseInt(args[2]), Long.parseLong(args[3])));
//        actorSystem.tell("PersistSharding");
//        actorSystem.tell("PoolReceptionist");
    }

    private static Config loadConfig() {
        LOGGER.info("Loading Akka Config...");
        Config config;
        String configPath = System.getProperty("config.file");
        if (configPath != null && new File(configPath).exists()) {
            config = ConfigFactory.load();
        } else {
            config = ConfigFactory.load("application.conf");
        }
        LOGGER.info("AkkaConfig: {}", config.root().unwrapped());
        return config;
    }
}
