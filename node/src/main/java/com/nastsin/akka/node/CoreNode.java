package com.nastsin.akka.node;

import akka.actor.typed.ActorSystem;
import com.nastsin.akka.node.actor.Initializer;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class CoreNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoreNode.class);

    public static void main(String[] args) {
        ActorSystem<String> actorSystem = ActorSystem.create(Initializer.create(), "akka-test-system", loadConfig());

        actorSystem.tell("TimerAkka");
        actorSystem.tell("CustomTimerAkka");
//        actorSystem.tell("PersistSharding");
        actorSystem.tell("PoolReceptionist");
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
