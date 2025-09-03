# 🐘 Configuración de Base de Datos PostgreSQL

## Paso 1: Iniciar Docker Desktop

1. **Abrir Docker Desktop** desde el menú de inicio o escritorio
2. **Esperar** a que Docker Desktop se inicie completamente (ícono en la bandeja del sistema)
3. **Verificar** que Docker esté corriendo ejecutando:
   ```bash
   docker info
   ```

## Paso 2: Iniciar PostgreSQL

Una vez que Docker Desktop esté corriendo, ejecuta:

### Opción A: Script automatizado (Windows)
```bash
scripts\start-db.bat
```

### Opción B: Comandos manuales
```bash
# Iniciar PostgreSQL
docker-compose up -d postgres

# Verificar que esté corriendo
docker-compose ps

# Ver logs si hay problemas
docker-compose logs postgres
```

## Paso 3: Verificar la Base de Datos

```bash
# Conectarse a la base de datos
docker-compose exec postgres psql -U tracking_user -d tracking_db

# Ver las tablas creadas
\dt

# Ver datos de prueba
SELECT * FROM tracking_events ORDER BY created_at DESC;

# Salir de psql
\q
```

## Paso 4: (Opcional) Iniciar Adminer

Para una interfaz web de administración:

```bash
docker-compose up -d adminer
```

Luego abrir: http://localhost:8081

**Datos de conexión:**
- Sistema: PostgreSQL
- Servidor: postgres
- Usuario: tracking_user
- Contraseña: tracking_pass
- Base de datos: tracking_db

## Información de Conexión

| Parámetro | Valor |
|-----------|-------|
| Host | localhost |
| Puerto | 5432 |
| Base de datos | tracking_db |
| Usuario | tracking_user |
| Contraseña | tracking_pass |

## Comandos Útiles

```bash
# Ver contenedores corriendo
docker-compose ps

# Ver logs de PostgreSQL
docker-compose logs postgres

# Detener servicios
docker-compose down

# Limpiar datos y reiniciar
docker-compose down -v
docker-compose up -d postgres

# Ejecutar consultas SQL directamente
docker-compose exec postgres psql -U tracking_user -d tracking_db -c "SELECT COUNT(*) FROM tracking_events;"
```

## Datos de Prueba Incluidos

La base de datos se inicializa automáticamente con:
- ✅ 4 tracking IDs únicos (TRK-TEST001 a TRK-TEST004)
- ✅ 3 usuarios diferentes (user123, user456, user789)
- ✅ 10 eventos de prueba con diferentes estados
- ✅ Metadata de ejemplo en formato JSON

## Troubleshooting

### Error: "Docker no está corriendo"
- Iniciar Docker Desktop manualmente
- Esperar a que aparezca el ícono en la bandeja del sistema

### Error: "Puerto 5432 ya está en uso"
- Detener PostgreSQL local si está corriendo
- O cambiar el puerto en docker-compose.yml

### Error: "No se puede conectar a la base de datos"
- Verificar que el contenedor esté corriendo: `docker-compose ps`
- Ver logs: `docker-compose logs postgres`
- Reiniciar: `docker-compose restart postgres`

## Próximo Paso

Una vez que PostgreSQL esté corriendo, puedes:
1. **Probar la aplicación Quarkus**: `./mvnw quarkus:dev`
2. **Acceder a Swagger**: http://localhost:8080/swagger-ui
3. **Probar los endpoints** con los datos de prueba incluidos