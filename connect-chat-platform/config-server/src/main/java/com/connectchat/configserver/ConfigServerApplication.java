package com.connectchat.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Local/offline-first config backplane. Native filesystem profile is the
 * default so the whole platform (and every agent's dev loop) runs without a
 * reachable Git-backed config server. See constitution principle III.
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
