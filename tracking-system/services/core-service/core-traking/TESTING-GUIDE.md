# 🧪 Guía Completa de Testing - Core Tracking Service

## 📋 Prerrequisitos

1. ✅ **Docker Desktop** instalado y corriendo
2. ✅ **Java 17** o superior
3. ✅ **Maven** 3.8+ (incluido en el wrapper)
4. ✅ **curl** para probar endpoints (incluido en Windows 10+)

## 🚀 Pasos para Probar el Sistema Completo

### Paso 1: Iniciar Docker Desktop
```bash
# Verificar que Docker esté corriendo
docker info
```

### Paso 2: Iniciar Base de Datos PostgreSQL
```bash
# Windows
scripts\start-db.bat

# Linux/Mac
./scripts/start-db.sh
```

**Resultado esperado:**
- ✅ PostgreSQL corriendo en puerto 5432
- ✅ Base de datos `tracking_db` creada
- ✅ Tabla `tracking_events` con datos de prueba
- ✅ 10 eventos de prueba insertados

### Paso 3: Iniciar la Aplicación Quarkus
```bash
# Ejecutar en modo desarrollo
./mvnw quarkus:dev

# O en Windows
mvnw.cmd quarkus:dev
```

**Resultado esperado:**
- ✅ Aplicación iniciada en puerto 8080
- ✅ Conexión exitosa a PostgreSQL
- ✅ Swagger UI disponible en http://localhost:8080/swagger-ui

### Paso 4: Verificar Health Checks
```bash
# Health check básico
curl http://localhost:8080/api/v1/health

# Health check detallado
curl http://localhost:8080/api/v1/health/detailed
```

**Resultado esperado:**
```json
{
  "service": "core-tracking-service",
  "status": "UP",
  "database": {
    "status": "UP",
    "type": "PostgreSQL"
  }
}
```

### Paso 5: Probar Endpoints con Datos de Prueba

#### 5.1 Obtener todos los trackings
```bash
curl http://localhost:8080/api/v1/tracking
```

#### 5.2 Obtener trackings por usuario
```bash
curl http://localhost:8080/api/v1/tracking/user/user123
```

#### 5.3 Obtener historial de tracking
```bash
curl http://localhost:8080/api/v1/tracking/TRK-TEST001/history
```

#### 5.4 Crear nuevo tracking
```bash
curl -X POST http://localhost:8080/api/v1/tracking \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "newuser",
    "status": "CREATED",
    "description": "Mi primer tracking",
    "metadata": "{\"source\": \"manual-test\"}"
  }'
```

#### 5.5 Actualizar status de tracking
```bash
curl -X PUT http://localhost:8080/api/v1/tracking/TRK-TEST001/status \
  -H "Content-Type: application/json" \
  -d '{
    "newStatus": "IN_PROGRESS",
    "description": "Actualizando status desde API"
  }'
```

### Paso 6: Usar Scripts Automatizados

#### Windows:
```bash
scripts\test-api.bat
```

#### Linux/Mac:
```bash
chmod +x scripts/test-api.sh
./scripts/test-api.sh
```

## 🌐 Interfaz Web (Swagger UI)

1. **Abrir navegador** en: http://localhost:8080/swagger-ui
2. **Explorar endpoints** disponibles
3. **Probar directamente** desde la interfaz
4. **Ver documentación** completa de cada endpoint

### Endpoints Disponibles:

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/v1/health` | Health check básico |
| `GET` | `/api/v1/health/detailed` | Health check con métricas |
| `POST` | `/api/v1/tracking` | Crear nuevo tracking |
| `PUT` | `/api/v1/tracking/{trackingId}/status` | Actualizar status |
| `GET` | `/api/v1/tracking/{trackingId}/history` | Historial de tracking |
| `GET` | `/api/v1/tracking/user/{userId}` | Trackings por usuario |
| `GET` | `/api/v1/tracking/status/{status}` | Trackings por status |
| `GET` | `/api/v1/tracking` | Todos los trackings |
| `GET` | `/api/v1/tracking/id/{id}` | Tracking por ID interno |

## 🗄️ Explorar Base de Datos

### Opción 1: Adminer (Interfaz Web)
```bash
docker-compose up -d adminer
```
Abrir: http://localhost:8081
- Sistema: PostgreSQL
- Servidor: postgres
- Usuario: tracking_user
- Contraseña: tracking_pass
- BD: tracking_db

### Opción 2: Línea de Comandos
```bash
# Conectar a PostgreSQL
docker-compose exec postgres psql -U tracking_user -d tracking_db

# Ver tablas
\dt

# Ver datos de prueba
SELECT * FROM tracking_events ORDER BY created_at DESC;

# Ver estadísticas
SELECT 
    status,
    COUNT(*) as cantidad,
    COUNT(DISTINCT user_id) as usuarios_unicos
FROM tracking_events 
GROUP BY status;

# Salir
\q
```

## 📊 Datos de Prueba Incluidos

La base de datos incluye automáticamente:

| Tracking ID | Usuario | Estados | Descripción |
|-------------|---------|---------|-------------|
| TRK-TEST001 | user123 | CREATED → IN_PROGRESS → COMPLETED | Flujo completo exitoso |
| TRK-TEST002 | user456 | CREATED → PROCESSING → FAILED | Flujo con error |
| TRK-TEST003 | user789 | CREATED → PENDING | Esperando aprobación |
| TRK-TEST004 | user123 | CREATED → IN_PROGRESS | En proceso |

## 🔧 Troubleshooting

### Error: "Connection refused"
- ✅ Verificar que PostgreSQL esté corriendo: `docker-compose ps`
- ✅ Verificar logs: `docker-compose logs postgres`

### Error: "Port 8080 already in use"
- ✅ Cambiar puerto en `application.properties`: `quarkus.http.port=8081`

### Error: "Table doesn't exist"
- ✅ Reiniciar PostgreSQL: `docker-compose restart postgres`
- ✅ Verificar script SQL: `docker-compose logs postgres`

### Error: "Docker not running"
- ✅ Iniciar Docker Desktop manualmente
- ✅ Esperar a que aparezca en la bandeja del sistema

## 🎯 Casos de Prueba Sugeridos

### 1. Flujo Completo de Tracking
1. Crear nuevo tracking
2. Actualizar status múltiples veces
3. Consultar historial completo
4. Verificar en base de datos

### 2. Validaciones
1. Intentar crear tracking sin userId (debe fallar)
2. Intentar actualizar tracking inexistente (debe fallar)
3. Usar status inválido (debe fallar)

### 3. Consultas Optimizadas
1. Consultar por usuario con muchos trackings
2. Filtrar por status específico
3. Verificar rendimiento con datos masivos

### 4. Metadata JSON
1. Crear tracking con metadata compleja
2. Consultar y verificar formato JSON
3. Actualizar con nueva metadata

## 📈 Métricas de Éxito

- ✅ **Health checks** retornan status "UP"
- ✅ **Todos los endpoints** responden correctamente
- ✅ **Base de datos** conecta sin errores
- ✅ **Swagger UI** carga completamente
- ✅ **Datos de prueba** se consultan correctamente
- ✅ **CRUD completo** funciona (Create, Read, Update)
- ✅ **Validaciones** funcionan apropiadamente
- ✅ **Historial** se mantiene correctamente

## 🚀 Siguiente Nivel

Una vez que todo funcione:
1. **Implementar JWT** para autenticación
2. **Agregar más tests** unitarios
3. **Configurar CI/CD** pipeline
4. **Optimizar queries** para producción
5. **Agregar métricas** con Prometheus