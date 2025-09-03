# Tracking Microservice

Microservicio de tracking desarrollado con **Quarkus** y **Clean Architecture**, diseñado para proporcionar trazabilidad completa de eventos con autenticación JWT y Vault.

## 🚀 Características Principales

- ✅ **Clean Architecture** con separación clara de responsabilidades
- ✅ **JDBC puro** para máximo control sobre queries SQL
- ✅ **PostgreSQL** con queries optimizadas e índices estratégicos
- ✅ **Documentación completa** con OpenAPI/Swagger
- ✅ **Validación robusta** de datos de entrada
- ✅ **Trazabilidad completa** con historial de cambios
- ✅ **Health checks** detallados
- ✅ **Preparado para JWT** y HashiCorp Vault

## 🏗️ Arquitectura

```
src/main/java/com/bt/core/tracking/
├── application/           # Capa de Aplicación
│   ├── configuration/     # Configuraciones
│   └── handler/          # Handlers de casos de uso
├── domain/               # Capa de Dominio
│   ├── model/           # Modelos del dominio
│   ├── ports/           # Interfaces (puertos)
│   └── service/         # Servicios del dominio
└── infrastructure/      # Capa de Infraestructura
    ├── adapters/
    │   └── persistence/ # Adaptadores de persistencia
    └── entrypoints/
        └── rest/        # Controladores REST
```

## 📊 Base de Datos

### Tabla Principal: `tracking_events`

```sql
CREATE TABLE tracking_events (
    id BIGSERIAL PRIMARY KEY,
    tracking_id VARCHAR(50) NOT NULL,
    user_id VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,
    description TEXT NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### Índices Optimizados

- `idx_tracking_events_tracking_id` - Para búsquedas por tracking ID
- `idx_tracking_events_user_id` - Para búsquedas por usuario
- `idx_tracking_events_status` - Para filtros por status
- `idx_tracking_events_timestamp` - Para ordenamiento temporal
- `idx_tracking_events_user_status` - Índice compuesto para consultas frecuentes

## 🔧 Configuración

### Variables de Entorno

```bash
# Base de Datos
DB_USERNAME=tracking_user
DB_PASSWORD=tracking_pass
DB_URL=jdbc:postgresql://localhost:5432/tracking_db

# JWT (preparado para futuro uso)
JWT_PUBLIC_KEY_LOCATION=META-INF/resources/publicKey.pem
JWT_ISSUER=https://core-tracking-service

# HashiCorp Vault (preparado para futuro uso)
VAULT_URL=http://localhost:8200
VAULT_ROLE=tracking-service
```

### Archivo application.properties

```properties
# Application Configuration
quarkus.application.name=core-tracking-service
quarkus.http.port=8080

# Database Configuration
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=${DB_USERNAME:tracking_user}
quarkus.datasource.password=${DB_PASSWORD:tracking_pass}
quarkus.datasource.jdbc.url=${DB_URL:jdbc:postgresql://localhost:5432/tracking_db}

# OpenAPI Configuration
quarkus.swagger-ui.always-include=true
quarkus.swagger-ui.path=/swagger-ui
```

## 🚀 Ejecución

### Desarrollo Local

```bash
# Clonar el repositorio
git clone <repository-url>
cd core-tracking-service

# Ejecutar en modo desarrollo
./mvnw quarkus:dev
```

### Docker

```bash
# Construir imagen nativa
./mvnw package -Pnative -Dquarkus.native.container-build=true

# Ejecutar con Docker
docker run -i --rm -p 8080:8080 core-tracking-service:1.0.0-SNAPSHOT
```

### Base de Datos PostgreSQL

```bash
# Ejecutar PostgreSQL con Docker
docker run --name postgres-tracking \
  -e POSTGRES_DB=tracking_db \
  -e POSTGRES_USER=tracking_user \
  -e POSTGRES_PASSWORD=tracking_pass \
  -p 5432:5432 \
  -d postgres:15
```

## 📚 API Endpoints

### Documentación Completa
- **Swagger UI**: http://localhost:8080/swagger-ui
- **OpenAPI Spec**: http://localhost:8080/openapi

### Endpoints Principales

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/v1/tracking` | Crear nuevo evento de tracking |
| `PUT` | `/api/v1/tracking/{trackingId}/status` | Actualizar status de tracking |
| `GET` | `/api/v1/tracking/{trackingId}/history` | Obtener historial de tracking |
| `GET` | `/api/v1/tracking/user/{userId}` | Obtener trackings por usuario |
| `GET` | `/api/v1/tracking/status/{status}` | Obtener trackings por status |
| `GET` | `/api/v1/tracking` | Obtener todos los trackings |
| `GET` | `/api/v1/tracking/id/{id}` | Obtener tracking por ID interno |

### Health Checks

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/v1/health` | Health check básico |
| `GET` | `/api/v1/health/detailed` | Health check detallado con métricas |

## 📝 Ejemplos de Uso

### Crear un nuevo tracking

```bash
curl -X POST http://localhost:8080/api/v1/tracking \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "status": "CREATED",
    "description": "Inicio de proceso de tracking",
    "metadata": "{\"source\": \"web\", \"ip\": \"192.168.1.1\"}"
  }'
```

### Actualizar status

```bash
curl -X PUT http://localhost:8080/api/v1/tracking/TRK-A1B2C3D4/status \
  -H "Content-Type: application/json" \
  -d '{
    "newStatus": "IN_PROGRESS",
    "description": "Proceso iniciado correctamente"
  }'
```

### Obtener historial

```bash
curl http://localhost:8080/api/v1/tracking/TRK-A1B2C3D4/history
```

## 🔍 Estados de Tracking

| Estado | Descripción |
|--------|-------------|
| `CREATED` | Evento creado inicialmente |
| `IN_PROGRESS` | Proceso en curso |
| `PROCESSING` | Procesando datos |
| `COMPLETED` | Completado exitosamente |
| `FAILED` | Falló durante el proceso |
| `CANCELLED` | Cancelado por el usuario |
| `PENDING` | Pendiente de aprobación |
| `APPROVED` | Aprobado |
| `REJECTED` | Rechazado |

## 🧪 Testing

```bash
# Ejecutar tests unitarios
./mvnw test

# Ejecutar tests de integración
./mvnw verify

# Ejecutar con cobertura
./mvnw test jacoco:report
```

## 📈 Monitoreo

### Health Checks
- **Básico**: `/api/v1/health`
- **Detallado**: `/api/v1/health/detailed`

### Métricas Incluidas
- Estado de la base de datos
- Métricas de memoria JVM
- Información del sistema
- Conectividad de dependencias

## 🔒 Seguridad

### Preparado para JWT
- Configuración lista para validación JWT
- Integración con HashiCorp Vault para secretos
- Validación completa de entrada
- Logs de auditoría

### Validaciones
- Validación de formato de datos
- Sanitización de entrada
- Manejo seguro de SQL queries
- Prevención de inyección SQL

## 🚀 Rendimiento

### Optimizaciones de Base de Datos
- Índices estratégicos para consultas frecuentes
- Queries SQL optimizadas y documentadas
- Paginación automática en consultas masivas
- Connection pooling configurado

### Optimizaciones de Aplicación
- Quarkus para startup rápido
- Compilación nativa disponible
- Manejo eficiente de memoria
- Logging optimizado

## 📋 Roadmap

- [ ] Implementación completa de JWT
- [ ] Integración con HashiCorp Vault
- [ ] Métricas con Prometheus
- [ ] Tracing distribuido
- [ ] Cache con Redis
- [ ] Eventos asíncronos

## 🤝 Contribución

1. Fork el proyecto
2. Crear feature branch (`git checkout -b feature/nueva-funcionalidad`)
3. Commit cambios (`git commit -am 'Agregar nueva funcionalidad'`)
4. Push al branch (`git push origin feature/nueva-funcionalidad`)
5. Crear Pull Request

## 📄 Licencia

Este proyecto es propiedad de BT. Todos los derechos reservados.

## 📞 Contacto

- **Equipo**: Core Tracking Team
- **Email**: core-tracking@bt.com
- **Documentación**: https://bt.com/core-tracking