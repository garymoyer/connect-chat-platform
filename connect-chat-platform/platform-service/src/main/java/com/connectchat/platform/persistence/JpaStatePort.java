package com.connectchat.platform.persistence;

import java.time.Instant;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class JpaStatePort implements StatePort {

    private final IdempotencyRecordRepository idempotencyRecordRepository;
    private final OutboxEventRepository outboxEventRepository;

    public JpaStatePort(IdempotencyRecordRepository idempotencyRecordRepository,
                         OutboxEventRepository outboxEventRepository) {
        this.idempotencyRecordRepository = idempotencyRecordRepository;
        this.outboxEventRepository = outboxEventRepository;
    }

    @Override
    @Transactional
    public boolean tryClaim(String correlationId) {
        if (idempotencyRecordRepository.existsById(correlationId)) {
            return false;
        }
        try {
            idempotencyRecordRepository.save(new IdempotencyRecord(correlationId, Instant.now()));
            return true;
        } catch (DataIntegrityViolationException raceLostToAnotherReplica) {
            return false;
        }
    }

    @Override
    @Transactional
    public void appendOutboxEvent(String aggregateType, String aggregateId, String eventType, String payloadJson) {
        outboxEventRepository.save(new OutboxEvent(aggregateType, aggregateId, eventType, payloadJson));
    }
}
