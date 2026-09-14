package com.connectchat.platform.config;

import java.util.Optional;

/**
 * Provider-agnostic read port for secrets. Never backed by plaintext values
 * in the config repo (constitution principle III) — the default local/dev
 * implementation resolves from the process environment; production
 * deployments swap in a Vault/KMS/cloud-secrets-manager-backed
 * implementation without any caller-side change.
 */
public interface SecretStore {

    Optional<String> getSecret(String name);
}
