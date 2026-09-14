package com.connectchat.platform.config;

import java.util.Optional;

/**
 * Provider-agnostic read port for configuration values. The default
 * implementation delegates to Spring's {@code Environment} (already
 * populated by Spring Cloud Config), so callers never depend on how or where
 * a value is ultimately stored (config-server, native file, a future
 * cloud-specific parameter store, etc.) — see constitution principle II/III.
 */
public interface ConfigStore {

    Optional<String> get(String key);

    String getOrDefault(String key, String defaultValue);

    boolean getBoolean(String key, boolean defaultValue);
}
