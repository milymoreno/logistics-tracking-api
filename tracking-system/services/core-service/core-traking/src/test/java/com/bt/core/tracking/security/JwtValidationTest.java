package com.bt.core.tracking.security;

import com.bt.core.tracking.infrastructure.security.VaultJwtValidator;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * Tests para validación JWT con mocks
 */
@QuarkusTest
public class JwtValidationTest {

    @InjectMock
    VaultJwtValidator jwtValidator;

    @BeforeEach
    public void setup() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    @DisplayName("Debe permitir acceso con token JWT válido")
    public void testValidJwtToken() {
        // Mock de validación exitosa
        VaultJwtValidator.JwtValidationResult validResult = 
            VaultJwtValidator.JwtValidationResult.valid(createMockClaims());
        
        Mockito.when(jwtValidator.validateToken(anyString()))
               .thenReturn(validResult);

        // Test con token válido
        given()
            .header("Authorization", "Bearer valid-jwt-token")
            .contentType("application/json")
        .when()
            .get("/api/v1/auth/test")
        .then()
            .statusCode(200)
            .body("message", equalTo("Authentication successful!"))
            .body("userId", equalTo("test-user-123"));
    }

    @Test
    @DisplayName("Debe rechazar token JWT inválido")
    public void testInvalidJwtToken() {
        // Mock de validación fallida
        VaultJwtValidator.JwtValidationResult invalidResult = 
            VaultJwtValidator.JwtValidationResult.invalid("Token signature invalid");
        
        Mockito.when(jwtValidator.validateToken(anyString()))
               .thenReturn(invalidResult);

        // Test con token inválido
        given()
            .header("Authorization", "Bearer invalid-jwt-token")
            .contentType("application/json")
        .when()
            .get("/api/v1/auth/test")
        .then()
            .statusCode(401)
            .body("error", equalTo("UNAUTHORIZED"))
            .body("message", equalTo("Invalid or expired token"));
    }

    @Test
    @DisplayName("Debe rechazar token JWT expirado")
    public void testExpiredJwtToken() {
        // Mock de token expirado
        VaultJwtValidator.JwtValidationResult expiredResult = 
            VaultJwtValidator.JwtValidationResult.invalid("Token expired");
        
        Mockito.when(jwtValidator.validateToken(anyString()))
               .thenReturn(expiredResult);

        // Test con token expirado
        given()
            .header("Authorization", "Bearer expired-jwt-token")
            .contentType("application/json")
        .when()
            .get("/api/v1/auth/test")
        .then()
            .statusCode(401)
            .body("error", equalTo("UNAUTHORIZED"))
            .body("message", equalTo("Invalid or expired token"));
    }

    @Test
    @DisplayName("Debe permitir acceso a tracking endpoints con token válido")
    public void testTrackingEndpointsWithValidToken() {
        // Mock de validación exitosa
        VaultJwtValidator.JwtValidationResult validResult = 
            VaultJwtValidator.JwtValidationResult.valid(createMockClaims());
        
        Mockito.when(jwtValidator.validateToken(anyString()))
               .thenReturn(validResult);

        // Test GET tracking por ID (aunque no exista, debe pasar la autenticación)
        given()
            .header("Authorization", "Bearer valid-jwt-token")
            .contentType("application/json")
        .when()
            .get("/api/v1/tracking/non-existent-id")
        .then()
            .statusCode(anyOf(equalTo(404), equalTo(500))) // No 401, significa que pasó la autenticación
            .body(not(containsString("UNAUTHORIZED")));

        // Test GET tracking por usuario
        given()
            .header("Authorization", "Bearer valid-jwt-token")
            .contentType("application/json")
        .when()
            .get("/api/v1/tracking/user/test-user")
        .then()
            .statusCode(anyOf(equalTo(200), equalTo(500))) // No 401, significa que pasó la autenticación
            .body(not(containsString("UNAUTHORIZED")));
    }

    @Test
    @DisplayName("Debe proporcionar información del usuario en endpoint /me")
    public void testUserInfoEndpoint() {
        // Mock de validación exitosa con información de usuario
        VaultJwtValidator.JwtValidationResult validResult = 
            VaultJwtValidator.JwtValidationResult.valid(createMockClaimsWithRole());
        
        Mockito.when(jwtValidator.validateToken(anyString()))
               .thenReturn(validResult);

        // Test endpoint /me
        given()
            .header("Authorization", "Bearer valid-jwt-token")
            .contentType("application/json")
        .when()
            .get("/api/v1/auth/me")
        .then()
            .statusCode(200)
            .body("userId", equalTo("admin-user-456"))
            .body("role", equalTo("admin"))
            .body("isAdmin", equalTo(true))
            .body("timestamp", notNullValue());
    }

    /**
     * Crea claims mock para pruebas básicas
     */
    private io.jsonwebtoken.Claims createMockClaims() {
        io.jsonwebtoken.Claims claims = Mockito.mock(io.jsonwebtoken.Claims.class);
        Mockito.when(claims.getSubject()).thenReturn("test-user-123");
        Mockito.when(claims.get("role")).thenReturn("user");
        return claims;
    }

    /**
     * Crea claims mock con rol de administrador
     */
    private io.jsonwebtoken.Claims createMockClaimsWithRole() {
        io.jsonwebtoken.Claims claims = Mockito.mock(io.jsonwebtoken.Claims.class);
        Mockito.when(claims.getSubject()).thenReturn("admin-user-456");
        Mockito.when(claims.get("role")).thenReturn("admin");
        return claims;
    }
}