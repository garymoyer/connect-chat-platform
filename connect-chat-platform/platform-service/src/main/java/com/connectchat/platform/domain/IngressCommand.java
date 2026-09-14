package com.connectchat.platform.domain;

/**
 * Channel-agnostic normalized command. Every ingress adapter (REST, webhook,
 * Kafka consumer, batch reader) maps its channel-specific request into this
 * shape before it reaches the domain layer — constitution principle V
 * (anti-corruption layer at every boundary).
 */
public record IngressCommand(String correlationId, String channel, String payload) {
}
