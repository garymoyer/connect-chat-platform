package com.connectchat.platform.ingress.rest;

import com.connectchat.platform.domain.IngressCommand;
import com.connectchat.platform.domain.IngressResult;
import com.connectchat.platform.domain.OrchestrationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST ingress adapter — one of several channel families (constitution
 * principle V: channel-specific shapes stop here, never reach the domain).
 * Correlation id is caller-suppliable (for channel-level retries to be
 * recognized as duplicates) or generated when absent.
 */
@RestController
@RequestMapping("/api/v1/ingress")
public class IngressRestController {

    private final OrchestrationService orchestrationService;

    public IngressRestController(OrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
    }

    @PostMapping
    public ResponseEntity<IngressResult> submit(
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationIdHeader,
            @Valid @RequestBody IngressRequest request) {

        String correlationId = (correlationIdHeader != null && !correlationIdHeader.isBlank())
                ? correlationIdHeader
                : UUID.randomUUID().toString();

        IngressResult result = orchestrationService.handle(
                new IngressCommand(correlationId, "rest", request.payload()));

        HttpStatus status = switch (result.outcome()) {
            case ACCEPTED_SETTLED -> HttpStatus.OK;
            case ACCEPTED_QUEUED_FOR_EGRESS -> HttpStatus.ACCEPTED;
            case DUPLICATE_IGNORED -> HttpStatus.OK;
            case REJECTED -> HttpStatus.UNPROCESSABLE_ENTITY;
        };

        return ResponseEntity.status(status).body(result);
    }

    public record IngressRequest(@NotBlank String payload) {
    }
}
