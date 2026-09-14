package com.connectchat.platform.persistence;

import java.time.Duration;
import java.util.Optional;

/**
 * Hot-path cache port (dedupe-key lookups, rate-limit counters). Backed by
 * Redis today; swappable or removable per deployment without touching
 * callers — constitution principle II.
 */
public interface CachePort {

    Optional<String> get(String key);

    void put(String key, String value, Duration ttl);

    void evict(String key);
}
