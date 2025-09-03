package com.bt.core.tracking.security;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Map;

/**
 * Perfil de test para configuración de seguridad
 */
public class SecurityTestProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        Map<String, String> config = new java.util.HashMap<>();
        
        // Habilitar seguridad en tests
        config.put("security.enabled", "true");
        
        // Configuración JWT para tests
        config.put("jwt.issuer", "bt-core-system-test");
        config.put("jwt.audience", "tracking-service-test");
        config.put("jwt.algorithm", "RS256");
        config.put("jwt.leeway.seconds", "30");
        
        // Configuración Vault para tests (mock)
        config.put("vault.url", "http://localhost:8200");
        config.put("vault.token", "test-token");
        config.put("vault.jwt.path", "auth/jwt/keys");
        config.put("vault.timeout", "5000");
        
        // Logging para tests
        config.put("quarkus.log.level", "INFO");
        config.put("quarkus.log.category.\"com.bt.core.tracking.security\".level", "DEBUG");
        
        // Deshabilitar health checks de Vault en tests
        config.put("quarkus.health.extensions.enabled", "false");
        
        return config;
    }

    @Override
    public String getConfigProfile() {
        return "test-security";
    }
}