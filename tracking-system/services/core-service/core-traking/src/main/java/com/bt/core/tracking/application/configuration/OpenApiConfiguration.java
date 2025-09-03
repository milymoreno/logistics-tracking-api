package com.bt.core.tracking.application.configuration;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.ws.rs.core.Application;

/**
 * Configuración completa de OpenAPI/Swagger para el Core Tracking Service
 * 
 * Esta configuración define toda la documentación de la API incluyendo:
 * - Información general del servicio
 * - Servidores disponibles
 * - Tags para organización de endpoints
 * - Información de contacto y licencia
 */
@OpenAPIDefinition(
    info = @Info(
        title = "Core Tracking Service API",
        version = "1.0.0",
        description = """
            # Core Tracking Service API
            
            API completa para el sistema de tracking core que permite:
            
            ## Funcionalidades Principales
            - ✅ **Creación de eventos de tracking** con IDs únicos generados automáticamente
            - ✅ **Actualización de status** manteniendo historial completo de cambios
            - ✅ **Consulta de historial** completo por tracking ID
            - ✅ **Búsquedas optimizadas** por usuario, status y otros criterios
            - ✅ **Trazabilidad completa** con timestamps y metadata
            - ✅ **Validación robusta** de datos de entrada
            - ✅ **Manejo de errores** detallado y consistente
            
            ## Arquitectura
            - **Clean Architecture** con separación clara de responsabilidades
            - **JDBC puro** para máximo control sobre queries SQL
            - **PostgreSQL** como base de datos con índices optimizados
            - **Quarkus** como framework reactivo de alto rendimiento
            
            ## Base de Datos
            - Queries SQL documentadas y optimizadas
            - Índices estratégicos para consultas frecuentes
            - Triggers automáticos para auditoría
            - Soporte para metadata en formato JSON
            
            ## Seguridad
            - Preparado para autenticación JWT
            - Integración con HashiCorp Vault para secretos
            - Validación completa de entrada
            - Logs detallados para auditoría
            
            ## Estados de Tracking Disponibles
            - `CREATED` - Evento creado inicialmente
            - `IN_PROGRESS` - Proceso en curso
            - `PROCESSING` - Procesando datos
            - `COMPLETED` - Completado exitosamente
            - `FAILED` - Falló durante el proceso
            - `CANCELLED` - Cancelado por el usuario
            - `PENDING` - Pendiente de aprobación
            - `APPROVED` - Aprobado
            - `REJECTED` - Rechazado
            
            ## Formato de Tracking ID
            Los tracking IDs se generan automáticamente con el formato: `TRK-XXXXXXXX`
            donde XXXXXXXX es un código alfanumérico único de 8 caracteres.
            """,
        contact = @Contact(
            name = "Core Tracking Team",
            email = "core-tracking@bt.com",
            url = "https://bt.com/core-tracking"
        ),
        license = @License(
            name = "Proprietary",
            url = "https://bt.com/license"
        )
    ),
    servers = {
        @Server(
            url = "http://localhost:8080",
            description = "Servidor de desarrollo local"
        ),
        @Server(
            url = "https://api-dev.bt.com",
            description = "Servidor de desarrollo"
        ),
        @Server(
            url = "https://api-staging.bt.com",
            description = "Servidor de staging"
        ),
        @Server(
            url = "https://api.bt.com",
            description = "Servidor de producción"
        )
    },
    tags = {
        @Tag(
            name = "Core Tracking API",
            description = """
                Endpoints principales para la gestión de tracking de eventos.
                
                Estos endpoints permiten el ciclo completo de vida de un evento de tracking:
                1. **Creación** de nuevos eventos
                2. **Actualización** de status manteniendo historial
                3. **Consulta** de eventos individuales o por criterios
                4. **Trazabilidad** completa con timestamps y metadata
                """
        ),
        @Tag(
            name = "Health & Monitoring",
            description = "Endpoints para monitoreo y health checks del servicio"
        )
    }
)
public class OpenApiConfiguration extends Application {
    // Esta clase solo sirve para la configuración de OpenAPI
    // No necesita implementación adicional
}