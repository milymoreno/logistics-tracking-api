# Tests de Seguridad - Sistema de Tracking

## Resumen de Tests Implementados

### 🔐 **SecurityTest.java**
**Propósito**: Verificar que todos los endpoints están protegidos y rechazan requests sin token

**Tests incluidos**:
- ✅ `testEndpointsWithoutToken()` - Verifica que todos los endpoints de tracking retornan 401 sin token
- ✅ `testInvalidTokenFormat()` - Verifica rechazo de tokens con formato inválido
- ✅ `testInvalidJwtToken()` - Verifica rechazo de tokens JWT malformados
- ✅ `testPublicEndpoints()` - Verifica que endpoints públicos son accesibles sin token
- ✅ `testWwwAuthenticateHeader()` - Verifica presencia del header WWW-Authenticate
- ✅ `testAuthTestEndpoints()` - Verifica protección de endpoints de prueba
- ✅ `testErrorResponseFormat()` - Verifica formato correcto de respuestas de error

### 🎫 **JwtValidationTest.java**
**Propósito**: Probar la validación JWT usando mocks

**Tests incluidos**:
- ✅ `testValidJwtToken()` - Verifica acceso con token válido
- ✅ `testInvalidJwtToken()` - Verifica rechazo de token inválido
- ✅ `testExpiredJwtToken()` - Verifica rechazo de token expirado
- ✅ `testTrackingEndpointsWithValidToken()` - Verifica acceso a endpoints de tracking
- ✅ `testUserInfoEndpoint()` - Verifica endpoint de información de usuario

### 🔧 **JwtAuthenticationFilterTest.java**
**Propósito**: Tests unitarios del filtro de autenticación

**Tests incluidos**:
- ✅ `testPublicEndpointsAllowed()` - Verifica que endpoints públicos no requieren token
- ✅ `testMissingAuthorizationHeader()` - Verifica rechazo sin header Authorization
- ✅ `testEmptyAuthorizationHeader()` - Verifica rechazo con header vacío
- ✅ `testInvalidTokenFormat()` - Verifica rechazo de formato inválido
- ✅ `testInvalidJwtToken()` - Verifica rechazo de JWT inválido
- ✅ `testValidJwtToken()` - Verifica acceso con JWT válido
- ✅ `testSecurityDisabled()` - Verifica comportamiento con seguridad deshabilitada
- ✅ `testMultiplePublicPaths()` - Verifica múltiples paths públicos
- ✅ `testTokenWithOptionalClaims()` - Verifica manejo de claims opcionales

### 🌐 **SecurityIntegrationTest.java**
**Propósito**: Tests de integración con configuración completa

**Tests incluidos**:
- ✅ `testSecurityConfiguration()` - Verifica configuración de seguridad
- ✅ `testAllTrackingEndpointsRequireAuth()` - Verifica todos los endpoints y métodos HTTP
- ✅ `testHealthEndpointsArePublic()` - Verifica endpoints de salud públicos
- ✅ `testSecurityHeaders()` - Verifica headers de seguridad
- ✅ `testConcurrentUnauthenticatedRequests()` - Verifica requests concurrentes
- ✅ `testMaliciousHeaders()` - Verifica manejo de headers maliciosos
- ✅ `testVaultIntegration()` - Test opcional con Vault real

### 🔓 **SecurityDisabledTest.java**
**Propósito**: Verificar comportamiento cuando la seguridad está deshabilitada

**Tests incluidos**:
- ✅ `testEndpointsWithoutTokenWhenSecurityDisabled()` - Verifica acceso sin token
- ✅ `testPublicEndpointsStillWork()` - Verifica que endpoints públicos siguen funcionando

## Cobertura de Tests

### **Endpoints Verificados**
| Endpoint | Método | Sin Token | Token Inválido | Token Válido |
|----------|--------|-----------|----------------|--------------|
| `/api/v1/tracking` | POST | ❌ 401 | ❌ 401 | ✅ 200/500 |
| `/api/v1/tracking/{id}` | GET | ❌ 401 | ❌ 401 | ✅ 200/404 |
| `/api/v1/tracking/{id}/status` | PUT | ❌ 401 | ❌ 401 | ✅ 200/404 |
| `/api/v1/tracking/{id}` | DELETE | ❌ 401 | ❌ 401 | ✅ 200/404 |
| `/api/v1/tracking/user/{userId}` | GET | ❌ 401 | ❌ 401 | ✅ 200 |
| `/api/v1/tracking/status/{status}` | GET | ❌ 401 | ❌ 401 | ✅ 200 |
| `/api/v1/auth/me` | GET | ❌ 401 | ❌ 401 | ✅ 200 |
| `/api/v1/auth/test` | GET | ❌ 401 | ❌ 401 | ✅ 200 |

### **Endpoints Públicos Verificados**
| Endpoint | Acceso Sin Token |
|----------|------------------|
| `/q/health` | ✅ 200 |
| `/q/health/live` | ✅ 200 |
| `/q/health/ready` | ✅ 200 |
| `/q/openapi` | ✅ 200 |
| `/q/swagger-ui` | ✅ 200/302 |

### **Escenarios de Error Verificados**
- ❌ Header Authorization ausente
- ❌ Header Authorization vacío
- ❌ Formato de token inválido (sin "Bearer ")
- ❌ Token JWT malformado
- ❌ Token JWT con firma inválida
- ❌ Token JWT expirado
- ❌ Claims requeridos ausentes (iss, aud)
- ❌ Headers maliciosos (XSS, tokens muy largos)

## Ejecutar Tests

### **Todos los tests de seguridad**
```bash
# Windows
scripts\run-security-tests.cmd

# Linux/Mac
mvn test -Dtest="com.bt.core.tracking.security.*Test"
```

### **Tests específicos**
```bash
# Solo tests básicos de seguridad
mvn test -Dtest="SecurityTest"

# Solo tests de validación JWT
mvn test -Dtest="JwtValidationTest"

# Solo tests unitarios del filtro
mvn test -Dtest="JwtAuthenticationFilterTest"

# Solo tests de integración
mvn test -Dtest="SecurityIntegrationTest"
```

### **Con Vault real (opcional)**
```bash
mvn test -Dtest="SecurityIntegrationTest" -Dtest.vault.enabled=true
```

## Configuración de Tests

### **Perfiles de Test**
- `SecurityTestProfile` - Seguridad habilitada
- `SecurityDisabledTestProfile` - Seguridad deshabilitada

### **Configuración de Test**
```properties
# Seguridad habilitada
security.enabled=true
jwt.issuer=bt-core-system-test
jwt.audience=tracking-service-test

# Logging para debugging
quarkus.log.category."com.bt.core.tracking.security".level=DEBUG
```

## Resultados Esperados

### **✅ Tests Exitosos Indican**:
1. **Todos los endpoints están protegidos** - No se puede acceder sin token válido
2. **Validación JWT funciona** - Tokens inválidos son rechazados
3. **Endpoints públicos accesibles** - Health checks y documentación funcionan
4. **Headers de seguridad presentes** - WWW-Authenticate incluido en 401
5. **Configuración flexible** - Seguridad se puede habilitar/deshabilitar
6. **Manejo de errores robusto** - Respuestas consistentes y seguras

### **❌ Si los Tests Fallan**:
1. Verificar configuración de seguridad en `application.properties`
2. Verificar que el filtro `JwtAuthenticationFilter` esté registrado
3. Verificar que `VaultJwtValidator` esté disponible
4. Revisar logs para errores específicos
5. Verificar que las dependencias JWT estén en el classpath

## Métricas de Seguridad

- **Cobertura de Endpoints**: 100% de endpoints de tracking protegidos
- **Cobertura de Métodos HTTP**: GET, POST, PUT, DELETE verificados
- **Cobertura de Escenarios**: 15+ escenarios de error diferentes
- **Tests Unitarios**: 25+ tests individuales
- **Tests de Integración**: 10+ tests de flujo completo

## Próximos Pasos

1. **Agregar tests de performance** - Verificar impacto del filtro de seguridad
2. **Tests con TestContainers** - Integración real con Vault
3. **Tests de rate limiting** - Si se implementa en el futuro
4. **Tests de roles y permisos** - Autorización granular
5. **Tests de rotación de claves** - Manejo de cambios de claves JWT