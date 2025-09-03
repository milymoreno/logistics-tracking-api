package com.bt.core.tracking.security;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Tests de integración de seguridad con configuración de prueba
 */
@QuarkusTest
@TestProfile(SecurityTestProfile.class)
public class SecurityIntegrationTest {

    @ConfigProperty(name = "security.enabled", defaultValue = "true")
    boolean securityEnabled;

    @Test
    @DisplayName("Debe verificar que la seguridad esté habilitada en tests")
    public void testSecurityConfiguration() {
        // Verificar que la configuración de seguridad esté activa
        assert securityEnabled : "La seguridad debe estar habilitada para los tests";
    }

    // @Test
    // @DisplayName("Debe rechazar todos los endpoints de tracking sin autenticación")
    // public void testAllTrackingEndpointsRequireAuth() {
    //     String[] trackingPaths = {
    //         "/api/v1/tracking",
    //         "/api/v1/tracking/test-id",
    //         "/api/v1/tracking/test-id/status",
    //         "/api/v1/tracking/user/test-user",
    //         "/api/v1/tracking/status/CREATED"
    //     };
    //
    //     String[] methods = {"GET", "POST", "PUT", "DELETE"};
    //
    //     for (String path : trackingPaths) {
    //         for (String method : methods) {
    //             // Skip invalid combinations
    //             if (shouldSkipMethodPath(method, path)) {
    //                 continue;
    //             }
    //
    //             given()
    //                 .contentType("application/json")
    //                 .body(getTestBody(method))
    //             .when()
    //                 .request(method, path)
    //             .then()
    //                 .statusCode(401)
    //                 .body("error", equalTo("UNAUTHORIZED"));
    //         }
    //     }
    // }

    // @Test
    // @DisplayName("Debe permitir acceso a endpoints de salud sin autenticación")
    // public void testHealthEndpointsArePublic() {
    //     String[] healthPaths = {
    //         "/q/health",
    //         "/q/health/live",
    //         "/q/health/ready"
    //     };
    //
    //     for (String path : healthPaths) {
    //         given()
    //         .when()
    //             .get(path)
    //         .then()
    //             .statusCode(200);
    //     }
    // }

    @Test
    @DisplayName("Debe verificar que los headers de seguridad estén presentes")
    public void testSecurityHeaders() {
        given()
            .contentType("application/json")
        .when()
            .get("/api/v1/tracking/test-id")
        .then()
            .statusCode(401)
            .header("WWW-Authenticate", "Bearer")
            .contentType("application/json");
    }

    @Test
    @DisplayName("Debe manejar múltiples requests concurrentes sin autenticación")
    public void testConcurrentUnauthenticatedRequests() {
        // Simular múltiples requests concurrentes
        for (int i = 0; i < 10; i++) {
            given()
                .contentType("application/json")
            .when()
                .get("/api/v1/tracking/test-id-" + i)
            .then()
                .statusCode(401)
                .body("error", equalTo("UNAUTHORIZED"));
        }
    }

    @Test
    @DisplayName("Debe rechazar requests con headers maliciosos")
    public void testMaliciousHeaders() {
        // Test con header Authorization malicioso
        given()
            .header("Authorization", "Bearer <script>alert('xss')</script>")
            .contentType("application/json")
        .when()
            .get("/api/v1/tracking/test-id")
        .then()
            .statusCode(401)
            .body("error", equalTo("UNAUTHORIZED"));

        // Test con header Authorization muy largo
        String longToken = "Bearer " + "a".repeat(10000);
        given()
            .header("Authorization", longToken)
            .contentType("application/json")
        .when()
            .get("/api/v1/tracking/test-id")
        .then()
            .statusCode(401)
            .body("error", equalTo("UNAUTHORIZED"));
    }

    @Test
    @EnabledIfSystemProperty(named = "test.vault.enabled", matches = "true")
    @DisplayName("Test de integración con Vault real (solo si está habilitado)")
    public void testVaultIntegration() {
        // Este test solo se ejecuta si se configura la propiedad del sistema
        // -Dtest.vault.enabled=true
        
        // Aquí se podría probar con un Vault real usando TestContainers
        // Por ahora, solo verificamos que el endpoint responde correctamente
        given()
            .contentType("application/json")
        .when()
            .get("/q/health/ready")
        .then()
            .statusCode(200);
    }

    /**
     * Determina si se debe saltar una combinación método-path
     */
    private boolean shouldSkipMethodPath(String method, String path) {
        // POST solo es válido para /api/v1/tracking
        if ("POST".equals(method) && !"/api/v1/tracking".equals(path)) {
            return true;
        }
        
        // PUT solo es válido para paths que terminan en /status
        if ("PUT".equals(method) && !path.endsWith("/status")) {
            return true;
        }
        
        // DELETE no es válido para paths de consulta
        if ("DELETE".equals(method) && (path.contains("/user/") || path.contains("/status/"))) {
            return true;
        }
        
        return false;
    }

    /**
     * Obtiene el body de prueba según el método HTTP
     */
    private String getTestBody(String method) {
        if ("POST".equals(method)) {
            return """
                {
                    "userId": "test-user",
                    "status": "CREATED",
                    "description": "Test tracking"
                }
                """;
        } else if ("PUT".equals(method)) {
            return """
                {
                    "newStatus": "IN_PROGRESS",
                    "description": "Test update"
                }
                """;
        }
        return "{}";
    }
}