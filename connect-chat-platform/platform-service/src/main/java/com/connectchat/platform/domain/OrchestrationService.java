package com.connectchat.platform.domain;

import com.connectchat.platform.egress.LegacyGatewayPort;
import com.connectchat.platform.egress.LegacyGatewayResult;
import com.connectchat.platform.persistence.StatePort;
import org.springframework.stereotype.Service;

/**
 * Channel- and legacy-protocol-agnostic core. Every ingress adapter calls in
 * here with a normalized {@link IngressCommand}; every legacy target is
 * reached only through {@link LegacyGatewayPort} (constitution principle V).
 */
@Service
public class OrchestrationService {

    private final StatePort statePort;
    private final LegacyGatewayPort legacyGatewayPort;

    public OrchestrationService(StatePort statePort, LegacyGatewayPort legacyGatewayPort) {
        this.statePort = statePort;
        this.legacyGatewayPort = legacyGatewayPort;
    }

    public IngressResult handle(IngressCommand command) {
        if (!statePort.tryClaim(command.correlationId())) {
            return new IngressResult(command.correlationId(), IngressResult.Outcome.DUPLICATE_IGNORED,
                    "correlationId already processed");
        }

        LegacyGatewayResult result = legacyGatewayPort.send(command.correlationId(), command.payload());

        if (result.success()) {
            return new IngressResult(command.correlationId(), IngressResult.Outcome.ACCEPTED_SETTLED,
                    result.message());
        }

        // Graceful degradation: legacy target is down/circuit open — durably
        // queue for async settlement instead of failing the caller.
        statePort.appendOutboxEvent("IngressCommand", command.correlationId(), "LegacyEgressRequested",
                command.payload());
        return new IngressResult(command.correlationId(), IngressResult.Outcome.ACCEPTED_QUEUED_FOR_EGRESS,
                "legacy target unavailable, queued: " + result.message());
    }
}
