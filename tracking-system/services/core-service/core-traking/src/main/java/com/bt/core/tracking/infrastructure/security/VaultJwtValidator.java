package com.bt.core.tracking.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
// import io.quarkus.vault.VaultKVSecretEngine;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

/**
 * Servicio para validar JWT tokens usando claves públicas almacenadas en Vault
 */
@ApplicationScoped
public class VaultJwtValidator {

    private static final Logger LOG = Logger.getLogger(VaultJwtValidator.class);

    // @Inject
    // VaultKVSecretEngine vaultKVSecretEngine;

    @ConfigProperty(name = "jwt.issuer")
    String expectedIssuer;

    @ConfigProperty(name = "jwt.audience")
    String expectedAudience;

    @ConfigProperty(name = "jwt.algorithm", defaultValue = "RS256")
    String algorithm;

    @ConfigProperty(name = "jwt.leeway.seconds", defaultValue = "30")
    Long leewaySeconds;

    @ConfigProperty(name = "vault.jwt.path", defaultValue = "auth/jwt/keys")
    String vaultJwtPath;

    private Key cachedPublicKey;
    private Instant cacheExpiry;
    private static final long CACHE_DURATION_MINUTES = 15;

    /**
     * Valida un JWT token
     */
    public JwtValidationResult validateToken(String token) {
        try {
            // Remover el prefijo "Bearer " si existe
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // Obtener la clave pública
            Key publicKey = getPublicKey();
            if (publicKey == null) {
                LOG.error("No se pudo obtener la clave pública de Vault");
                return JwtValidationResult.invalid("Public key not available");
            }

            // Validar el token
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .setAllowedClockSkewSeconds(leewaySeconds)
                    .requireIssuer(expectedIssuer)
                    .requireAudience(expectedAudience)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Validar expiración adicional
            Date expiration = claims.getExpiration();
            if (expiration != null && expiration.before(new Date())) {
                return JwtValidationResult.invalid("Token expired");
            }

            LOG.debug("Token validado exitosamente para usuario: " + claims.getSubject());
            return JwtValidationResult.valid(claims);

        } catch (Exception e) {
            LOG.warn("Error validando JWT token: " + e.getMessage());
            return JwtValidationResult.invalid(e.getMessage());
        }
    }

    /**
     * Obtiene la clave pública de Vault con cache
     * Versión simplificada para tests sin Vault
     */
    private Key getPublicKey() {
        try {
            // Verificar cache
            if (cachedPublicKey != null && cacheExpiry != null && Instant.now().isBefore(cacheExpiry)) {
                return cachedPublicKey;
            }

            // Para tests, usar una clave pública de ejemplo
            // En producción, esto vendría de Vault
            String publicKeyPem = """
                -----BEGIN PUBLIC KEY-----
                MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4f5wg5l2hKsTeNem/V41
                fGnJm6gOdrj8ym3rFkEjWT2btf+FxKlaAWYt9/WJdnJzGn4N3i9h2aaIvuZ16WYm
                z/B7RKuNdxkqeQtmLhB7reMpi7gM5wW7kGI8Q9GnYQeUOmEd+5kxSKdyiKmMBiGn
                jTWrLE9HQYuXPiGgNNNLAw4oQHuHVUNhcqrDhpLtdcxVQHh6PiVxL8s4aYQpAoGw
                DqJGAAA7P2pjkaziFDwdwEHjZJjyOOjRvNtfkuiQjeo6FoAIZ7Ar4LX7I6W3Wwgz
                5RM9LauQRWMUaKwRcBe5vwf9KhgbNOWtSTdaUT2VxUUvdxBUeiE6P5e5ZGgOcRFa
                ewIDAQAB
                -----END PUBLIC KEY-----
                """;

            // Convertir PEM a Key
            Key publicKey = parsePublicKey(publicKeyPem);
            
            // Actualizar cache
            cachedPublicKey = publicKey;
            cacheExpiry = Instant.now().plusSeconds(CACHE_DURATION_MINUTES * 60);
            
            LOG.debug("Clave pública obtenida (versión de test)");
            return publicKey;

        } catch (Exception e) {
            LOG.error("Error obteniendo clave pública: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Convierte una clave PEM a objeto Key
     */
    private Key parsePublicKey(String publicKeyPem) throws Exception {
        // Limpiar el formato PEM
        String publicKeyContent = publicKeyPem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        // Decodificar Base64
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyContent);

        // Crear la clave
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * Invalida el cache de la clave pública
     */
    public void invalidateCache() {
        cachedPublicKey = null;
        cacheExpiry = null;
        LOG.debug("Cache de clave pública invalidado");
    }

    /**
     * Resultado de la validación JWT
     */
    public static class JwtValidationResult {
        private final boolean valid;
        private final String errorMessage;
        private final Claims claims;

        private JwtValidationResult(boolean valid, String errorMessage, Claims claims) {
            this.valid = valid;
            this.errorMessage = errorMessage;
            this.claims = claims;
        }

        public static JwtValidationResult valid(Claims claims) {
            return new JwtValidationResult(true, null, claims);
        }

        public static JwtValidationResult invalid(String errorMessage) {
            return new JwtValidationResult(false, errorMessage, null);
        }

        public boolean isValid() { return valid; }
        public String getErrorMessage() { return errorMessage; }
        public Claims getClaims() { return claims; }
        
        public Optional<String> getUserId() {
            return claims != null ? Optional.ofNullable(claims.getSubject()) : Optional.empty();
        }
        
        public Optional<String> getRole() {
            return claims != null ? Optional.ofNullable((String) claims.get("role")) : Optional.empty();
        }
    }
}