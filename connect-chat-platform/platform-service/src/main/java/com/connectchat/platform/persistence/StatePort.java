package com.connectchat.platform.persistence;

/**
 * Durable-state port for anything that must survive a node restart or be
 * shared across replicas: idempotency/dedupe claims and the outbox used to
 * hand off events to egress adapters asynchronously. Concrete datastore
 * (H2 locally, any relational store in production) stays swappable behind
 * this interface — constitution principle X (statelessness).
 */
public interface StatePort {

    /**
     * Atomically claims a correlation id for processing.
     *
     * @return true if this call is the first to claim it (safe to proceed),
     *         false if it has already been claimed (duplicate — an ingress
     *         retry from the same channel, most likely).
     */
    boolean tryClaim(String correlationId);

    /**
     * Appends an event to the outbox for asynchronous, at-least-once
     * hand-off to an egress adapter. Used when a legacy target is degraded
     * or the domain allows async settlement (see NFR: graceful degradation).
     */
    void appendOutboxEvent(String aggregateType, String aggregateId, String eventType, String payloadJson);
}
