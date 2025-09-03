package com.bt.core.tracking.infrastructure.security;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.util.Set;

/**
 * Filtro de autenticación JWT que valida tokens en todas las requests
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthenticationFilter implements ContainerRequestFilter {

    private static final Logger LOG = Logger.getLogger(JwtAuthenticationFilter.class);

    @Inject
    VaultJwtValidator jwtValidator;

    @ConfigProperty(name = "security.enabled", defaultValue = "true")
    boolean securityEnabled;

    @ConfigProperty(name = "security.log.failed.attempts", defaultValue = "true")
    boolean logFailedAttempts;

    // Endpoints que no requieren autenticación
    private static final Set<String> PUBLIC_PATHS = Set.of(
        "/q/health",
        "/q/health/live",
        "/q/health/ready",
        "/q/swagger-ui",
        "/q/openapi",
        "/swagger-ui",
        "/api/v1/health"
    );

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        
        // Si la seguridad está deshabilitada, permitir acceso
        if (!securityEnabled) {
            LOG.debug("Seguridad deshabilitada, permitiendo acceso");
            return;
        }

        String path = requestContext.getUriInfo().getPath();
        String method = requestContext.getMethod();

        // Permitir acceso a endpoints públicos
        if (isPublicPath(path)) {
            LOG.debug("Acceso permitido a endpoint público: " + path);
            return;
        }

        // Obtener el token del header Authorization
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        
        if (authHeader == null || authHeader.trim().isEmpty()) {
            LOG.warn("Request sin token de autorización a: " + method + " " + path);
            abortWithUnauthorized(requestContext, "Missing Authorization header");
            return;
        }

        if (!authHeader.startsWith("Bearer ")) {
            LOG.warn("Formato de token inválido en: " + method + " " + path);
            abortWithUnauthorized(requestContext, "Invalid token format. Expected 'Bearer <token>'");
            return;
        }

        // Validar el token
        VaultJwtValidator.JwtValidationResult result = jwtValidator.validateToken(authHeader);
        
        if (!result.isValid()) {
            if (logFailedAttempts) {
                LOG.warn("Token inválido para: " + method + " " + path + " - " + result.getErrorMessage());
            }
            abortWithUnauthorized(requestContext, "Invalid or expired token");
            return;
        }

        // Token válido - agregar información del usuario al contexto
        result.getUserId().ifPresent(userId -> 
            requestContext.setProperty("jwt.userId", userId));
        
        result.getRole().ifPresent(role -> 
            requestContext.setProperty("jwt.role", role));

        LOG.debug("Acceso autorizado para usuario: " + result.getUserId().orElse("unknown") + 
                 " a: " + method + " " + path);
    }

    /**
     * Verifica si el path es público (no requiere autenticación)
     */
    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(publicPath -> 
            path.startsWith(publicPath) || path.equals(publicPath.substring(1)));
    }

    /**
     * Aborta la request con error 401 Unauthorized
     */
    private void abortWithUnauthorized(ContainerRequestContext requestContext, String message) {
        Response response = Response.status(Response.Status.UNAUTHORIZED)
                .entity(new ErrorResponse("UNAUTHORIZED", message))
                .header("WWW-Authenticate", "Bearer")
                .build();
        
        requestContext.abortWith(response);
    }

    /**
     * Clase para respuestas de error
     */
    public static class ErrorResponse {
        public String error;
        public String message;
        public long timestamp;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }
    }
}