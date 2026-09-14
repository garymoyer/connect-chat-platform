package com.connectchat.platform.config;

import java.util.Optional;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class SpringEnvironmentConfigStore implements ConfigStore {

    private final Environment environment;

    public SpringEnvironmentConfigStore(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Optional<String> get(String key) {
        return Optional.ofNullable(environment.getProperty(key));
    }

    @Override
    public String getOrDefault(String key, String defaultValue) {
        return environment.getProperty(key, defaultValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return environment.getProperty(key, Boolean.class, defaultValue);
    }
}
