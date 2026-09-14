package com.connectchat.platform.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.connectchat.platform.egress.LegacyGatewayPort;
import com.connectchat.platform.egress.LegacyGatewayResult;
import com.connectchat.platform.persistence.StatePort;
import org.junit.jupiter.api.Test;

class OrchestrationServiceTest {

    private final StatePort statePort = mock(StatePort.class);
    private final LegacyGatewayPort legacyGatewayPort = mock(LegacyGatewayPort.class);
    private final OrchestrationService service = new OrchestrationService(statePort, legacyGatewayPort);

    @Test
    void duplicateCorrelationIdIsIgnoredWithoutCallingEgress() {
        when(statePort.tryClaim("dup-1")).thenReturn(false);

        IngressResult result = service.handle(new IngressCommand("dup-1", "rest", "payload"));

        assertThat(result.outcome()).isEqualTo(IngressResult.Outcome.DUPLICATE_IGNORED);
        verifyNoMoreInteractions(legacyGatewayPort);
    }

    @Test
    void successfulEgressSettlesImmediately() {
        when(statePort.tryClaim("ok-1")).thenReturn(true);
        when(legacyGatewayPort.send("ok-1", "payload")).thenReturn(LegacyGatewayResult.ok("done"));

        IngressResult result = service.handle(new IngressCommand("ok-1", "rest", "payload"));

        assertThat(result.outcome()).isEqualTo(IngressResult.Outcome.ACCEPTED_SETTLED);
    }

    @Test
    void failedEgressDegradesGracefullyToOutbox() {
        when(statePort.tryClaim("degraded-1")).thenReturn(true);
        when(legacyGatewayPort.send("degraded-1", "payload"))
                .thenReturn(LegacyGatewayResult.failed("circuit open"));

        IngressResult result = service.handle(new IngressCommand("degraded-1", "rest", "payload"));

        assertThat(result.outcome()).isEqualTo(IngressResult.Outcome.ACCEPTED_QUEUED_FOR_EGRESS);
        verify(statePort).appendOutboxEvent("IngressCommand", "degraded-1", "LegacyEgressRequested", "payload");
    }
}
