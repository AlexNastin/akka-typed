package com.nastsin.akka.seed;

import akka.actor.typed.ActorSystem;
import com.nastsin.akka.seed.actor.ClusterListener;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class SeedNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeedNode.class);

    public static void main(String[] args) {
        ActorSystem.create(ClusterListener.create(), "akka-test-system", loadConfig());
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