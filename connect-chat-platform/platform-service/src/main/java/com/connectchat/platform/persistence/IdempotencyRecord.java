package com.connectchat.platform.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.Instant;

@Entity
public class IdempotencyRecord {

    @Id
    private String correlationId;

    private Instant claimedAt;

    protected IdempotencyRecord() {
        // JPA
    }

    public IdempotencyRecord(String correlationId, Instant claimedAt) {
        this.correlationId = correlationId;
        this.claimedAt = claimedAt;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public Instant getClaimedAt() {
        return claimedAt;
    }
}
