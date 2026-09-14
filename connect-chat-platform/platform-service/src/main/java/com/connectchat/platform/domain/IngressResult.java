package com.connectchat.platform.domain;

public record IngressResult(String correlationId, Outcome outcome, String message) {

    public enum Outcome {
        ACCEPTED_SETTLED,
        ACCEPTED_QUEUED_FOR_EGRESS,
        DUPLICATE_IGNORED,
        REJECTED
    }
}
