package com.connectchat.platform.egress.soap;

import com.connectchat.platform.egress.LegacyGatewayPort;
import com.connectchat.platform.egress.LegacyGatewayResult;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Component;

/**
 * Anti-corruption-layer adapter for the SOAP-based legacy target
 * (constitution principle V — no SOAP/XML type ever escapes this package).
 *
 * TODO(implementation-agent): wire the real SOAP client (e.g. Spring-WS or
 * a generated JAX-WS stub) per the approved plan for this legacy target.
 * The stub below only proves the resilience wiring (timeout/retry/circuit
 * breaker/bulkhead, all instance-named "legacy-soap-gateway" and configured
 * in config-repo/platform-service.yml) and the fallback contract.
 */
@Component
public class SoapLegacyGatewayAdapter implements LegacyGatewayPort {

    @Override
    @CircuitBreaker(name = "legacy-soap-gateway", fallbackMethod = "fallback")
    @Retry(name = "legacy-soap-gateway")
    @Bulkhead(name = "legacy-soap-gateway")
    public LegacyGatewayResult send(String correlationId, String payload) {
        throw new UnsupportedOperationException(
                "SOAP client not yet implemented — see TODO above and the platform-foundation spec");
    }

    private LegacyGatewayResult fallback(String correlationId, String payload, Throwable throwable) {
        return LegacyGatewayResult.failed(
                "legacy-soap-gateway unavailable for correlationId=" + correlationId + ": " + throwable.getMessage());
    }
}
