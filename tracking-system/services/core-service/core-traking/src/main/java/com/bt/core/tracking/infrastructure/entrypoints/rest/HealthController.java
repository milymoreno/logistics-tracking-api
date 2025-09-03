package com.bt.core.tracking.infrastructure.entrypoints.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para health checks y monitoreo del servicio
 */
@Path("/api/v1/health")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Health & Monitoring", description = "Endpoints para verificar el estado del servicio y sus dependencias")
public class HealthController {
    
    @Inject
    DataSource dataSource;
    
    /**
     * Health check básico del servicio
     */
    @GET
    @Operation(
        summary = "Health check básico",
        description = "Verifica que el servicio esté funcionando correctamente y pueda conectarse a la base de datos"
    )
    @APIResponse(responseCode = "200", description = "Servicio funcionando correctamente")
    @APIResponse(responseCode = "503", description = "Servicio no disponible - problemas con dependencias")
    public Response healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("service", "core-tracking-service");
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("version", "1.0.0");
        
        // Verificar conexión a base de datos
        Map<String, Object> database = new HashMap<>();
        try (Connection conn = dataSource.getConnection()) {
            database.put("status", "UP");
            database.put("type", "PostgreSQL");
            database.put("connection", "OK");
        } catch (SQLException e) {
            database.put("status", "DOWN");
            database.put("error", e.getMessage());
            health.put("status", "DOWN");
        }
        
        health.put("database", database);
        
        // Determinar código de respuesta basado en el status
        Response.Status responseStatus = "UP".equals(health.get("status")) ? 
            Response.Status.OK : Response.Status.SERVICE_UNAVAILABLE;
        
        return Response.status(responseStatus).entity(health).build();
    }
    
    /**
     * Health check detallado con métricas
     */
    @GET
    @Path("/detailed")
    @Operation(
        summary = "Health check detallado",
        description = "Proporciona información detallada sobre el estado del servicio, métricas y dependencias"
    )

    public Response detailedHealthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("service", "core-tracking-service");
        health.put("timestamp", LocalDateTime.now());
        health.put("version", "1.0.0");
        health.put("environment", System.getProperty("quarkus.profile", "dev"));
        
        // Información del sistema
        Map<String, Object> system = new HashMap<>();
        Runtime runtime = Runtime.getRuntime();
        system.put("javaVersion", System.getProperty("java.version"));
        system.put("maxMemory", runtime.maxMemory() / 1024 / 1024 + " MB");
        system.put("totalMemory", runtime.totalMemory() / 1024 / 1024 + " MB");
        system.put("freeMemory", runtime.freeMemory() / 1024 / 1024 + " MB");
        system.put("availableProcessors", runtime.availableProcessors());
        health.put("system", system);
        
        // Estado de la base de datos con métricas
        Map<String, Object> database = new HashMap<>();
        try (Connection conn = dataSource.getConnection()) {
            database.put("status", "UP");
            database.put("type", "PostgreSQL");
            database.put("url", conn.getMetaData().getURL());
            database.put("driver", conn.getMetaData().getDriverName());
            database.put("driverVersion", conn.getMetaData().getDriverVersion());
            
            // Test query para verificar que las tablas existen
            try (var stmt = conn.prepareStatement("SELECT COUNT(*) FROM tracking_events LIMIT 1")) {
                stmt.executeQuery();
                database.put("tablesAccessible", true);
            } catch (SQLException e) {
                database.put("tablesAccessible", false);
                database.put("tableError", e.getMessage());
            }
            
        } catch (SQLException e) {
            database.put("status", "DOWN");
            database.put("error", e.getMessage());
        }
        
        health.put("database", database);
        
        // Configuración de la aplicación
        Map<String, Object> config = new HashMap<>();
        config.put("httpPort", System.getProperty("quarkus.http.port", "8080"));
        config.put("profile", System.getProperty("quarkus.profile", "dev"));
        health.put("configuration", config);
        
        return Response.ok(health).build();
    }
}