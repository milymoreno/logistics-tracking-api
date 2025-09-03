package com.bt.core.tracking.security;

import com.bt.core.tracking.infrastructure.security.JwtAuthenticationFilter;
import com.bt.core.tracking.infrastructure.security.VaultJwtValidator;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para JwtAuthenticationFilter
 */
public class JwtAuthenticationFilterTest {

    @Mock
    private VaultJwtValidator jwtValidator;

    @Mock
    private ContainerRequestContext requestContext;

    @Mock
    private UriInfo uriInfo;

    @InjectMocks
    private JwtAuthenticationFilter authFilter;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Configurar mocks básicos
        when(requestContext.getUriInfo()).thenReturn(uriInfo);
        when(requestContext.getMethod()).thenReturn("GET");
        
        // Configurar el filtro con seguridad habilitada
        try {
            java.lang.reflect.Field securityEnabledField = JwtAuthenticationFilter.class.getDeclaredField("securityEnabled");
            securityEnabledField.setAccessible(true);
            securityEnabledField.set(authFilter, true);
            
            java.lang.reflect.Field logFailedAttemptsField = JwtAuthenticationFilter.class.getDeclaredField("logFailedAttempts");
            logFailedAttemptsField.setAccessible(true);
            logFailedAttemptsField.set(authFilter, true);
        } catch (Exception e) {
            // Ignorar si no se puede configurar
        }
    }

    @Test
    @DisplayName("Debe permitir acceso a endpoints públicos sin token")
    public void testPublicEndpointsAllowed() throws IOException {
        // Test health endpoint
        when(uriInfo.getPath()).thenReturn("q/health");
        
        authFilter.filter(requestContext);
        
        verify(requestContext, never()).abortWith(any(Response.class));
    }

    @Test
    @DisplayName("Debe abortar request sin header Authorization")
    public void testMissingAuthorizationHeader() throws IOException {
        when(uriInfo.getPath()).thenReturn("api/v1/tracking/test");
        when(requestContext.getHeaderString("Authorization")).thenReturn(null);
        
        authFilter.filter(requestContext);
        
        verify(requestContext).abortWith(any(Response.class));
    }

    @Test
    @DisplayName("Debe abortar request con header Authorization vacío")
    public void testEmptyAuthorizationHeader() throws IOException {
        when(uriInfo.getPath()).thenReturn("api/v1/tracking/test");
        when(requestContext.getHeaderString("Authorization")).thenReturn("");
        
        authFilter.filter(requestContext);
        
        verify(requestContext).abortWith(any(Response.class));
    }

    @Test
    @DisplayName("Debe abortar request con formato de token inválido")
    public void testInvalidTokenFormat() throws IOException {
        when(uriInfo.getPath()).thenReturn("api/v1/tracking/test");
        when(requestContext.getHeaderString("Authorization")).thenReturn("InvalidFormat token");
        
        authFilter.filter(requestContext);
        
        verify(requestContext).abortWith(any(Response.class));
    }

    @Test
    @DisplayName("Debe abortar request con token JWT inválido")
    public void testInvalidJwtToken() throws IOException {
        when(uriInfo.getPath()).thenReturn("api/v1/tracking/test");
        when(requestContext.getHeaderString("Authorization")).thenReturn("Bearer invalid-token");
        
        VaultJwtValidator.JwtValidationResult invalidResult = 
            VaultJwtValidator.JwtValidationResult.invalid("Invalid token");
        when(jwtValidator.validateToken(anyString())).thenReturn(invalidResult);
        
        authFilter.filter(requestContext);
        
        verify(requestContext).abortWith(any(Response.class));
        verify(jwtValidator).validateToken("Bearer invalid-token");
    }

    @Test
    @DisplayName("Debe permitir acceso con token JWT válido")
    public void testValidJwtToken() throws IOException {
        when(uriInfo.getPath()).thenReturn("api/v1/tracking/test");
        when(requestContext.getHeaderString("Authorization")).thenReturn("Bearer valid-token");
        
        // Mock de claims válidos
        io.jsonwebtoken.Claims mockClaims = mock(io.jsonwebtoken.Claims.class);
        when(mockClaims.getSubject()).thenReturn("test-user");
        when(mockClaims.get("role")).thenReturn("user");
        
        VaultJwtValidator.JwtValidationResult validResult = 
            VaultJwtValidator.JwtValidationResult.valid(mockClaims);
        when(jwtValidator.validateToken(anyString())).thenReturn(validResult);
        
        authFilter.filter(requestContext);
        
        verify(requestContext, never()).abortWith(any(Response.class));
        verify(requestContext).setProperty("jwt.userId", "test-user");
        verify(requestContext).setProperty("jwt.role", "user");
        verify(jwtValidator).validateToken("Bearer valid-token");
    }

    @Test
    @DisplayName("Debe permitir acceso cuando la seguridad está deshabilitada")
    public void testSecurityDisabled() throws IOException {
        // Deshabilitar seguridad
        try {
            java.lang.reflect.Field securityEnabledField = JwtAuthenticationFilter.class.getDeclaredField("securityEnabled");
            securityEnabledField.setAccessible(true);
            securityEnabledField.set(authFilter, false);
        } catch (Exception e) {
            // Ignorar si no se puede configurar
        }
        
        when(uriInfo.getPath()).thenReturn("api/v1/tracking/test");
        when(requestContext.getHeaderString("Authorization")).thenReturn(null);
        
        authFilter.filter(requestContext);
        
        verify(requestContext, never()).abortWith(any(Response.class));
        verify(jwtValidator, never()).validateToken(anyString());
    }

    @Test
    @DisplayName("Debe manejar múltiples paths públicos correctamente")
    public void testMultiplePublicPaths() throws IOException {
        String[] publicPaths = {
            "q/health",
            "q/health/live", 
            "q/health/ready",
            "q/swagger-ui",
            "q/openapi",
            "swagger-ui"
        };
        
        for (String path : publicPaths) {
            reset(requestContext);
            when(requestContext.getUriInfo()).thenReturn(uriInfo);
            when(uriInfo.getPath()).thenReturn(path);
            
            authFilter.filter(requestContext);
            
            verify(requestContext, never()).abortWith(any(Response.class));
        }
    }

    @Test
    @DisplayName("Debe manejar token con claims opcionales")
    public void testTokenWithOptionalClaims() throws IOException {
        when(uriInfo.getPath()).thenReturn("api/v1/tracking/test");
        when(requestContext.getHeaderString("Authorization")).thenReturn("Bearer valid-token");
        
        // Mock de claims sin rol
        io.jsonwebtoken.Claims mockClaims = mock(io.jsonwebtoken.Claims.class);
        when(mockClaims.getSubject()).thenReturn("test-user");
        when(mockClaims.get("role")).thenReturn(null);
        
        VaultJwtValidator.JwtValidationResult validResult = 
            VaultJwtValidator.JwtValidationResult.valid(mockClaims);
        when(jwtValidator.validateToken(anyString())).thenReturn(validResult);
        
        authFilter.filter(requestContext);
        
        verify(requestContext, never()).abortWith(any(Response.class));
        verify(requestContext).setProperty("jwt.userId", "test-user");
        verify(requestContext, never()).setProperty(eq("jwt.role"), any());
    }
}