package com.connectchat.platform.config;

import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * Default {@link SecretStore}: resolves secrets from the process
 * environment. Adequate for local dev and CI; production profiles must
 * supply a {@link SecretStore} bean backed by Vault/KMS/cloud secrets
 * manager (any bean of that type takes priority over this one — see
 * {@code @ConditionalOnMissingBean}).
 */
@Component
@ConditionalOnMissingBean(SecretStore.class)
public class EnvironmentSecretStore implements SecretStore {

    @Override
    public Optional<String> getSecret(String name) {
        return Optional.ofNullable(System.getenv(name));
    }
}
