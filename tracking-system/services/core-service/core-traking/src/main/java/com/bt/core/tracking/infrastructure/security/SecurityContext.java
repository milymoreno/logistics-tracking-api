package com.bt.core.tracking.infrastructure.security;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;

import java.util.Optional;

/**
 * Contexto de seguridad para obtener información del usuario autenticado
 */
@RequestScoped
public class SecurityContext {

    @Context
    ContainerRequestContext requestContext;

    /**
     * Obtiene el ID del usuario autenticado
     */
    public Optional<String> getCurrentUserId() {
        if (requestContext == null) {
            return Optional.empty();
        }
        
        Object userId = requestContext.getProperty("jwt.userId");
        return userId != null ? Optional.of(userId.toString()) : Optional.empty();
    }

    /**
     * Obtiene el rol del usuario autenticado
     */
    public Optional<String> getCurrentUserRole() {
        if (requestContext == null) {
            return Optional.empty();
        }
        
        Object role = requestContext.getProperty("jwt.role");
        return role != null ? Optional.of(role.toString()) : Optional.empty();
    }

    /**
     * Verifica si el usuario actual tiene un rol específico
     */
    public boolean hasRole(String expectedRole) {
        return getCurrentUserRole()
                .map(role -> role.equalsIgnoreCase(expectedRole))
                .orElse(false);
    }

    /**
     * Verifica si el usuario actual es el propietario del recurso
     */
    public boolean isOwner(String resourceUserId) {
        return getCurrentUserId()
                .map(currentUserId -> currentUserId.equals(resourceUserId))
                .orElse(false);
    }

    /**
     * Verifica si el usuario tiene permisos de administrador
     */
    public boolean isAdmin() {
        return hasRole("admin") || hasRole("administrator");
    }
}