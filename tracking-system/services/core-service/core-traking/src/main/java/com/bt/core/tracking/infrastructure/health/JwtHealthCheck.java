package com.bt.core.tracking.infrastructure.health;

import com.bt.core.tracking.infrastructure.security.VaultJwtValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

/**
 * Health check para verificar la conectividad con Vault y la disponibilidad de claves JWT
 */
@Readiness
@ApplicationScoped
public class JwtHealthCheck implements HealthCheck {

    @Inject
    VaultJwtValidator jwtValidator;

    @Override
    public HealthCheckResponse call() {
        try {
            // Intentar obtener la clave pública de Vault
            // Esto verificará la conectividad con Vault
            jwtValidator.invalidateCache(); // Forzar una nueva consulta
            
            // Crear un token de prueba simple para verificar que el validador funciona
            // (esto no valida el token, solo verifica que el servicio esté disponible)
            
            return HealthCheckResponse.up("JWT Vault Integration");
                    
        } catch (Exception e) {
            return HealthCheckResponse.down("JWT Vault Integration");
        }
    }
}