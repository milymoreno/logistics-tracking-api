# Seguridad - Sistema de Tracking

## Autenticación JWT con Vault

El sistema implementa autenticación basada en **JWT (JSON Web Tokens)** con validación de firmas usando claves almacenadas en **HashiCorp Vault**.

## Configuración

### Variables de Entorno (.env)

```bash
# Vault Configuration
VAULT_URL=http://vault:8200
VAULT_TOKEN=your-vault-token-here
VAULT_JWT_PATH=auth/jwt/keys

# JWT Configuration
JWT_ISSUER=bt-core-system
JWT_AUDIENCE=tracking-service
JWT_ALGORITHM=RS256
JWT_LEEWAY_SECONDS=30

# Security Configuration
SECURITY_ENABLED=true
SECURITY_LOG_FAILED_ATTEMPTS=true
```

### Configuración en application.properties

```properties
# Vault Configuration
vault.url=${VAULT_URL:http://vault:8200}
vault.token=${VAULT_TOKEN:}
vault.jwt.path=${VAULT_JWT_PATH:auth/jwt/keys}

# JWT Configuration
jwt.issuer=${JWT_ISSUER:bt-core-system}
jwt.audience=${JWT_AUDIENCE:tracking-service}
jwt.algorithm=${JWT_ALGORITHM:RS256}
jwt.leeway.seconds=${JWT_LEEWAY_SECONDS:30}

# Security Configuration
security.enabled=${SECURITY_ENABLED:true}
```

## Uso de la API

### Formato del Token

Todos los endpoints (excepto los públicos) requieren un token JWT en el header:

```http
Authorization: Bearer <jwt-token>
```

### Endpoints Públicos

Los siguientes endpoints NO requieren autenticación:

- `/q/health/*` - Health checks
- `/q/swagger-ui/*` - Documentación Swagger
- `/q/openapi` - Especificación OpenAPI

### Endpoints Protegidos

Todos los endpoints bajo `/api/v1/tracking/*` requieren autenticación válida.

## Estructura del JWT

### Header
```json
{
  "alg": "RS256",
  "typ": "JWT"
}
```

### Payload (Claims)
```json
{
  "sub": "user-id-123",
  "iss": "bt-core-system",
  "aud": "tracking-service",
  "exp": 1640995200,
  "iat": 1640991600,
  "role": "user",
  "name": "Usuario Ejemplo"
}
```

### Claims Requeridos

| Claim | Descripción | Requerido |
|-------|-------------|-----------|
| `sub` | ID del usuario | ✅ |
| `iss` | Emisor del token | ✅ |
| `aud` | Audiencia del token | ✅ |
| `exp` | Fecha de expiración | ✅ |
| `iat` | Fecha de emisión | ✅ |
| `role` | Rol del usuario | ❌ |

## Configuración de Vault

### 1. Levantar Vault

```bash
docker-compose up vault -d
```

### 2. Configurar Vault

```bash
chmod +x scripts/setup-vault.sh
./scripts/setup-vault.sh
```

### 3. Verificar Configuración

```bash
# Verificar que Vault esté funcionando
curl http://localhost:8200/v1/sys/health

# Verificar que las claves estén almacenadas
export VAULT_ADDR=http://localhost:8200
export VAULT_TOKEN=myroot
vault kv get secret/tracking/jwt
```

## Validación de Tokens

### Proceso de Validación

1. **Extracción**: Se extrae el token del header `Authorization`
2. **Formato**: Se verifica el formato `Bearer <token>`
3. **Clave Pública**: Se obtiene la clave pública de Vault (con cache)
4. **Firma**: Se valida la firma RSA256
5. **Claims**: Se validan los claims requeridos:
   - `iss` (issuer)
   - `aud` (audience)
   - `exp` (expiration)
6. **Contexto**: Se almacena información del usuario en el contexto de la request

### Cache de Claves

- Las claves públicas se cachean por **15 minutos**
- El cache se invalida automáticamente si hay errores
- Se puede invalidar manualmente para forzar actualización

## Manejo de Errores

### Códigos de Respuesta

| Código | Descripción | Causa |
|--------|-------------|-------|
| 401 | Unauthorized | Token ausente, inválido o expirado |
| 403 | Forbidden | Token válido pero sin permisos |
| 500 | Internal Server Error | Error interno del sistema |

### Respuesta de Error

```json
{
  "error": "UNAUTHORIZED",
  "message": "Invalid or expired token",
  "timestamp": 1640991600000
}
```

## Endpoints de Prueba

### Verificar Autenticación

```bash
# Obtener información del usuario autenticado
curl -H "Authorization: Bearer <token>" \
     http://localhost:8080/api/v1/auth/me

# Endpoint de prueba simple
curl -H "Authorization: Bearer <token>" \
     http://localhost:8080/api/v1/auth/test
```

### Respuesta Exitosa

```json
{
  "userId": "test-user-123",
  "role": "user",
  "isAdmin": false,
  "timestamp": 1640991600000
}
```

## Health Checks

### Verificar Estado de JWT/Vault

```bash
curl http://localhost:8080/q/health/ready
```

### Respuesta

```json
{
  "status": "UP",
  "checks": [
    {
      "name": "JWT Vault Integration",
      "status": "UP",
      "data": {
        "vault_connection": "OK",
        "jwt_validator": "Ready"
      }
    }
  ]
}
```

## Seguridad Adicional

### Recomendaciones

1. **Rotación de Claves**: Rotar las claves JWT periódicamente
2. **HTTPS**: Usar HTTPS en producción
3. **Timeouts**: Configurar timeouts apropiados para Vault
4. **Logging**: Monitorear intentos de acceso fallidos
5. **Rate Limiting**: Implementar rate limiting si es necesario

### Configuración de Producción

```properties
# Producción
vault.url=https://vault.production.com
vault.timeout=3000
jwt.leeway.seconds=10
security.log.failed.attempts=true
```

## Troubleshooting

### Problemas Comunes

1. **Token expirado**: Verificar claim `exp`
2. **Clave pública no encontrada**: Verificar configuración de Vault
3. **Issuer inválido**: Verificar claim `iss`
4. **Audience inválido**: Verificar claim `aud`

### Logs Útiles

```bash
# Ver logs del servicio
docker-compose logs tracking-service

# Ver logs de Vault
docker-compose logs vault
```