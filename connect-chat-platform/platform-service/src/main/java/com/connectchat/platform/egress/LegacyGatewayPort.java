package com.connectchat.platform.egress;

/**
 * Common port every legacy-backend adapter implements (SOAP, mainframe/MQ,
 * legacy REST/DB, ...). Adding a new legacy target is additive — a new
 * adapter implementing this port — never invasive to the domain layer.
 * Every implementation MUST declare its own timeout/retry/circuit-breaker
 * policy under its own resilience4j instance name (constitution principle
 * VII); see config-repo/platform-service.yml for the per-target instances.
 */
public interface LegacyGatewayPort {

    LegacyGatewayResult send(String correlationId, String payload);
}
