package com.connectchat.platform;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
class PlatformServiceApplicationTests {

    @Test
    void contextLoads() {
        // Fails fast if the ingress -> domain -> egress -> persistence wiring
        // (config, JPA, Redis, Kafka autoconfiguration) is broken.
    }
}
