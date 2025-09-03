package com.bt.core.tracking.security;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Tests de seguridad para verificar que los endpoints están protegidos
 */
@QuarkusTest
public class SecurityTest {

    @Test
    @DisplayName("Debe rechazar requests sin token de autorización")
    public void testEndpointsWithoutToken() {
        // Test POST /api/v1/tracking
        given()
            .contentType("application/json")
            .body("""
                {
                    "userId": "test-user",
                    "status": "CREATED",
                    "description": "Test tracking"
                }
                """)
        .when()
            .post("/api/v1/tracking")
        .then()
            .statusCode(401)
            .body("error", equalTo("UNAUTHORIZED"))
            .body("message", containsString("Missing Authorization header"));

        // Test GET /api/v1/tracking/{id}
        given()
        .when()
            .get("/api/v1/tracking/test-id")
        .then()
            .statusCode(401)
            .body("error", equalTo("UNAUTHORIZED"));

        // Test PUT /api/v1/tracking/{id}/status
        given()
            .contentType("application/json")
            .body("""
                {
                    "newStatus": "IN_PROGRESS",
                    "description": "Test update"
                }
                """)
        .when()
            .put("/api/v1/tracking/test-id/status")
        .then()
            .statusCode(401)
            .body("error", equalTo("UNAUTHORIZED"));

        // Test DELETE /api/v1/tracking/{id}
        given()
        .when()
            .delete("/api/v1/tracking/test-id")
        .then()
            .statusCode(401)
            .body("error", equalTo("UNAUTHORIZED"));

        // Test GET /api/v1/tracking/user/{userId}
        given()
        .when()
            .get("/api/v1/tracking/user/test-user")
        .then()
            .statusCode(401)
            .body("error", equalTo("UNAUTHORIZED"));

        // Test GET /api/v1/tracking/status/{status}
        given()
        .when()
            .get("/api/v1/tracking/status/CREATED")
        .then()
            .statusCode(401)
            .body("error", equalTo("UNAUTHORIZED"));
    }

    @Test
    @DisplayName("Debe rechazar tokens con formato inválido")
    public void testInvalidTokenFormat() {
        // Token sin "Bearer " prefix
        given()
            .header("Authorization", "invalid-token")
            .contentType("application/json")
        .when()
            .get("/api/v1/tracking/test-id")
        .then()
            .statusCode(401)
            .body("error", equalTo("UNAUTHORIZED"))
            .body("message", containsString("Invalid token format"));

        // Token vacío
        given()
            .header("Authorization", "")
            .contentType("application/json")
        .when()
            .get("/api/v1/tracking/test-id")
        .then()
            .statusCode(401)
            .body("error", equalTo("UNAUTHORIZED"))
            .body("message", containsString("Missing Authorization header"));

        // Token solo con "Bearer"
        given()
            .header("Authorization", "Bearer")
            .contentType("application/json")
        .when()
            .get("/api/v1/tracking/test-id")
        .then()
            .statusCode(401)
            .body("error", equalTo("UNAUTHORIZED"));
    }

    @Test
    @DisplayName("Debe rechazar tokens JWT inválidos")
    public void testInvalidJwtToken() {
        // Token JWT malformado
        given()
            .header("Authorization", "Bearer invalid.jwt.token")
            .contentType("application/json")
        .when()
            .get("/api/v1/tracking/test-id")
        .then()
            .statusCode(401)
            .body("error", equalTo("UNAUTHORIZED"))
            .body("message", equalTo("Invalid or expired token"));

        // Token JWT con formato correcto pero firma inválida
        String invalidJwt = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9." +
                           "eyJzdWIiOiJ0ZXN0LXVzZXIiLCJpc3MiOiJidC1jb3JlLXN5c3RlbSIsImF1ZCI6InRyYWNraW5nLXNlcnZpY2UifQ." +
                           "invalid-signature";

        given()
            .header("Authorization", "Bearer " + invalidJwt)
            .contentType("application/json")
        .when()
            .get("/api/v1/tracking/test-id")
        .then()
            .statusCode(401)
            .body("error", equalTo("UNAUTHORIZED"))
            .body("message", equalTo("Invalid or expired token"));
    }

    @Test
    @DisplayName("Debe permitir acceso a endpoints públicos sin token")
    public void testPublicEndpoints() {
        // Health check endpoints
        given()
        .when()
            .get("/q/health")
        .then()
            .statusCode(200);

        given()
        .when()
            .get("/q/health/live")
        .then()
            .statusCode(200);

        given()
        .when()
            .get("/q/health/ready")
        .then()
            .statusCode(200);

        // OpenAPI endpoints
        given()
        .when()
            .get("/q/openapi")
        .then()
            .statusCode(200);

        // Swagger UI (puede retornar 200 o 302 dependiendo de la configuración)
        given()
        .when()
            .get("/q/swagger-ui")
        .then()
            .statusCode(anyOf(equalTo(200), equalTo(302)));
    }

    @Test
    @DisplayName("Debe incluir header WWW-Authenticate en respuestas 401")
    public void testWwwAuthenticateHeader() {
        given()
            .contentType("application/json")
        .when()
            .get("/api/v1/tracking/test-id")
        .then()
            .statusCode(401)
            .header("WWW-Authenticate", "Bearer");
    }

    @Test
    @DisplayName("Debe proteger endpoints de autenticación de prueba")
    public void testAuthTestEndpoints() {
        // Test /api/v1/auth/me
        given()
        .when()
            .get("/api/v1/auth/me")
        .then()
            .statusCode(401)
            .body("error", equalTo("UNAUTHORIZED"));

        // Test /api/v1/auth/test
        given()
        .when()
            .get("/api/v1/auth/test")
        .then()
            .statusCode(401)
            .body("error", equalTo("UNAUTHORIZED"));
    }

    @Test
    @DisplayName("Debe validar que la respuesta de error tenga el formato correcto")
    public void testErrorResponseFormat() {
        given()
            .contentType("application/json")
        .when()
            .get("/api/v1/tracking/test-id")
        .then()
            .statusCode(401)
            .body("error", notNullValue())
            .body("message", notNullValue())
            .body("timestamp", notNullValue())
            .body("timestamp", instanceOf(Number.class));
    }
}