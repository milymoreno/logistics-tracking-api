package com.bt.core.tracking.security;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Tests para verificar el comportamiento cuando la seguridad está deshabilitada
 */
@QuarkusTest
@TestProfile(SecurityDisabledTest.SecurityDisabledTestProfile.class)
public class SecurityDisabledTest {

    @Test
    @DisplayName("Debe permitir acceso sin token cuando la seguridad está deshabilitada")
    public void testEndpointsWithoutTokenWhenSecurityDisabled() {
        // Test GET tracking por ID (aunque no exista, debe pasar sin autenticación)
        given()
            .contentType("application/json")
        .when()
            .get("/api/v1/tracking/test-id")
        .then()
            .statusCode(anyOf(equalTo(404), equalTo(500))) // No 401, significa que no requiere autenticación
            .body(not(containsString("UNAUTHORIZED")));

        // Test GET tracking por usuario
        given()
            .contentType("application/json")
        .when()
            .get("/api/v1/tracking/user/test-user")
        .then()
            .statusCode(anyOf(equalTo(200), equalTo(500))) // No 401, significa que no requiere autenticación
            .body(not(containsString("UNAUTHORIZED")));

        // Test endpoint de autenticación
        given()
            .contentType("application/json")
        .when()
            .get("/api/v1/auth/test")
        .then()
            .statusCode(anyOf(equalTo(200), equalTo(500))) // No 401, significa que no requiere autenticación
            .body(not(containsString("UNAUTHORIZED")));
    }

    // @Test
    // @DisplayName("Debe seguir permitiendo acceso a endpoints públicos")
    // public void testPublicEndpointsStillWork() {
    //     // Health checks deben seguir funcionando
    //     given()
    //     .when()
    //         .get("/q/health")
    //     .then()
    //         .statusCode(200);
    //
    //     given()
    //     .when()
    //         .get("/q/health/live")
    //     .then()
    //         .statusCode(200);
    //
    //     // OpenAPI debe seguir funcionando
    //     given()
    //     .when()
    //         .get("/q/openapi")
    //     .then()
    //         .statusCode(200);
    // }

    /**
     * Perfil de test con seguridad deshabilitada
     */
    public static class SecurityDisabledTestProfile implements io.quarkus.test.junit.QuarkusTestProfile {
        
        @Override
        public java.util.Map<String, String> getConfigOverrides() {
            java.util.Map<String, String> config = new java.util.HashMap<>();
            
            // Deshabilitar seguridad
            config.put("security.enabled", "false");
            
            // Configuración básica
            config.put("jwt.issuer", "bt-core-system-test");
            config.put("jwt.audience", "tracking-service-test");
            
            // Logging
            config.put("quarkus.log.level", "INFO");
            
            return config;
        }

        @Override
        public String getConfigProfile() {
            return "test-no-security";
        }
    }
}