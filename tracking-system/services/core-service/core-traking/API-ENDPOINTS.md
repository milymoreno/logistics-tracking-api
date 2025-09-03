# API Endpoints - Sistema de Tracking

## Base URL
```
http://localhost:8080/api/v1/tracking
```

## Endpoints Disponibles

### 📝 CRUD Operations

#### 1. Crear Evento de Tracking
```http
POST /api/v1/tracking
Content-Type: application/json

{
  "userId": "user123",
  "status": "CREATED",
  "description": "Nuevo evento de tracking",
  "metadata": {
    "source": "web",
    "priority": "high"
  }
}
```

**Response (201):**
```json
{
  "trackingId": "TRK-20250901-001",
  "userId": "user123",
  "status": "CREATED",
  "description": "Nuevo evento de tracking",
  "metadata": {
    "source": "web",
    "priority": "high"
  },
  "createdAt": "2025-01-09T10:30:00Z",
  "updatedAt": "2025-01-09T10:30:00Z"
}
```

#### 2. Obtener Tracking por ID
```http
GET /api/v1/tracking/{trackingId}
```

**Ejemplo:**
```http
GET /api/v1/tracking/TRK-20250901-001
```

#### 3. Actualizar Status de Tracking
```http
PUT /api/v1/tracking/{trackingId}/status
Content-Type: application/json

{
  "newStatus": "IN_PROGRESS",
  "description": "Procesando evento"
}
```

#### 4. Eliminar Tracking
```http
DELETE /api/v1/tracking/{trackingId}
```

### 🔍 Query Operations

#### 5. Obtener Trackings por Usuario
```http
GET /api/v1/tracking/user/{userId}
```

**Ejemplo:**
```http
GET /api/v1/tracking/user/user123
```

#### 6. Obtener Trackings por Status
```http
GET /api/v1/tracking/status/{status}
```

**Ejemplo:**
```http
GET /api/v1/tracking/status/IN_PROGRESS
```

## Status Disponibles

| Status | Descripción |
|--------|-------------|
| `CREATED` | Evento creado |
| `IN_PROGRESS` | En proceso |
| `COMPLETED` | Completado |
| `FAILED` | Falló |
| `CANCELLED` | Cancelado |

## Códigos de Respuesta

| Código | Descripción |
|--------|-------------|
| 200 | OK - Operación exitosa |
| 201 | Created - Recurso creado |
| 204 | No Content - Eliminación exitosa |
| 400 | Bad Request - Datos inválidos |
| 404 | Not Found - Recurso no encontrado |
| 500 | Internal Server Error - Error del servidor |

## Documentación Swagger

Una vez que el servidor esté ejecutándose, puedes acceder a la documentación interactiva en:

```
http://localhost:8080/q/swagger-ui
```

## Ejemplos de Uso con cURL

### Crear tracking:
```bash
curl -X POST http://localhost:8080/api/v1/tracking \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "status": "CREATED",
    "description": "Test tracking event",
    "metadata": {"source": "api"}
  }'
```

### Obtener tracking:
```bash
curl -X GET http://localhost:8080/api/v1/tracking/TRK-20250901-001
```

### Actualizar status:
```bash
curl -X PUT http://localhost:8080/api/v1/tracking/TRK-20250901-001/status \
  -H "Content-Type: application/json" \
  -d '{
    "newStatus": "COMPLETED",
    "description": "Proceso finalizado"
  }'
```

### Obtener por usuario:
```bash
curl -X GET http://localhost:8080/api/v1/tracking/user/user123
```