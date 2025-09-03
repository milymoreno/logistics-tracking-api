package com.bt.core.tracking.infrastructure.entrypoints.rest;

import com.bt.core.tracking.infrastructure.security.SecurityContext;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller para probar la autenticación JWT
 */
@Path("/api/v1/auth")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Authentication", description = "Endpoints para probar la autenticación")
public class AuthTestController {

    @Inject
    SecurityContext securityContext;

    @GET
    @Path("/me")
    @Operation(
        summary = "Obtener información del usuario autenticado",
        description = "Retorna información del usuario basada en el JWT token"
    )
    @SecurityRequirement(name = "bearerAuth")
    public Response getCurrentUser() {
        Map<String, Object> userInfo = new HashMap<>();
        
        userInfo.put("userId", securityContext.getCurrentUserId().orElse("unknown"));
        userInfo.put("role", securityContext.getCurrentUserRole().orElse("user"));
        userInfo.put("isAdmin", securityContext.isAdmin());
        userInfo.put("timestamp", System.currentTimeMillis());
        
        return Response.ok(userInfo).build();
    }

    @GET
    @Path("/test")
    @Operation(
        summary = "Endpoint de prueba protegido",
        description = "Endpoint simple para probar que la autenticación funciona"
    )
    @SecurityRequirement(name = "bearerAuth")
    public Response testAuth() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Authentication successful!");
        response.put("userId", securityContext.getCurrentUserId().orElse("unknown"));
        response.put("timestamp", System.currentTimeMillis());
        
        return Response.ok(response).build();
    }
}