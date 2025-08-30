# Plan de Trabajo - Reto Técnico API REST

**Fecha de elaboración:** 30/08/2025
**Deadline:** Miércoles 6:00 p.m.

---

## **1. Decisiones de arquitectura**

| **Tema**               | **Decisión**                                                                                     | **Justificación**                                                                              |
| ---------------------- | ------------------------------------------------------------------------------------------------ | ---------------------------------------------------------------------------------------------- |
| **Estilo**             | **Hexagonal + CQRS lógico**                                                                      | Separación clara de comandos (POST) y queries (GET) dentro del mismo microservicio.            |
| **Microservicios**     | 1 micro inicial (**core-service**) + 1 micro **auth-service**                                    | Core para endpoints principales; Auth para emitir/validar JWT RS256 con JWKS.                  |
| **Lenguajes**          | **Core-service: Java 21 + Quarkus + Mutiny**<br>**Auth-service: Node.js + TypeScript + Fastify** | Quarkus reactive para alta concurrencia; Node.js/TS con arquitectura hexagonal.                |
| **Contenedores**       | Sí, con **Docker Compose** (Auth, Core, Vault, Postgres, API Gateway).                           |
| **Gestor de secretos** | **Vault** (en contenedor)                                                                        | Guarda claves privadas JWT, recuperadas en Auth.                                               |
| **Seguridad**          | JWT **RS256** + JWKS público                                                                     | Auth-service (Node.js + jsonwebtoken) emite; Core (Quarkus SmallRye JWT) valida reactivamente. |
| **Swagger/OpenAPI**    | Obligatorio, expuesto en `/docs`.                                                                |
| **CI/CD**              | **GitHub Actions** + **Sonar** + **Docker Build & Deploy**                                       | Pipeline con gates de calidad y pruebas automáticas.                                           |
| **Testing**            | Unitarias + integración + contract tests                                                         | Con Testcontainers (Postgres, Vault).                                                          |

---

## **2. Atributos de calidad (NFRs)**

| **Atributo**       | **Estrategia**                          | **Implementación**                                                  |
| ------------------ | --------------------------------------- | ------------------------------------------------------------------- |
| **Resiliencia**    | Circuit Breaker + Retry + Timeout       | SmallRye Fault Tolerance (reactive); axios-retry en Node.js         |
| **Disponibilidad** | Health checks + Graceful shutdown       | Quarkus Health `/q/health`; Fastify hooks                           |
| **Mantenibilidad** | Clean Architecture + SOLID + DDD        | Hexagonal en ambos servicios: Quarkus (Java) + Node.js (TypeScript) |
| **Observabilidad** | Logs estructurados + Métricas + Tracing | Quarkus Logging JSON + Micrometer + OpenTelemetry                   |
| **Performance**    | Reactive streams + Non-blocking I/O     | Reactive PostgreSQL + Redis reactive + Mutiny                       |
| **Escalabilidad**  | Stateless + Load balancing              | Horizontal scaling con Docker replicas                              |

---

## **3. Endpoints confirmados**

| **Método** | **Ruta**                        | **Caso de uso**           | **Descripción**                                                    |
| ---------- | ------------------------------- | ------------------------- | ------------------------------------------------------------------ |
| **POST**   | `/api/v1/checkpoints`           | `CreateCheckpointHandler` | Registrar un checkpoint (unidad + estado + timestamp).             |
| **GET**    | `/api/v1/tracking/{shipmentId}` | `GetTrackingHandler`      | Devuelve estado agregado de la **guía** y el historial por unidad. |
| **GET**    | `/api/v1/shipments`             | `ListShipmentsHandler`    | Listar guías filtradas por **estado agregado**.                    |

---

## **4. Modelo de datos**

### **Tablas principales**

- **states** → Catálogo normalizado de estados.
- **shipments** → Datos maestros de la guía.
- **shipment_units** → Cada unidad asociada a la guía.
- **unit_checkpoint_log** → Bitácora de checkpoints por unidad.
- **unit_current_state** → Estado actual por unidad.
- **shipment_current_state** → Estado agregado de la guía.

---

## **5. Casos de uso y puertos**

### Casos de uso

- `CreateCheckpointCommandHandler`
- `GetTrackingQueryHandler`
- `ListShipmentsQueryHandler`

### Puertos principales

- `UnitCheckpointRepositoryPort`
- `UnitStateQueryPort`
- `ShipmentStateQueryPort`
- `TokenVerifierPort`
- `IdempotencyStorePort`

---

## **6. Diagramas necesarios**

| **Diagrama**          | **Propósito**                                                        |
| --------------------- | -------------------------------------------------------------------- |
| **C4 Nivel 1**        | Vista de contexto: actores, microservicios, gateway.                 |
| **C4 Nivel 2**        | Vista de contenedores: Core, Auth, Vault, DB, API Gateway.           |
| **C4 Nivel 3**        | Componentes Core: controladores, casos de uso, puertos, adaptadores. |
| **Flujo JWT**         | Cómo Auth emite y Core valida tokens RS256.                          |
| **Flujo Checkpoints** | Bitácora, actualización de estado de unidad y agregado de guía.      |
| **CI/CD**             | Pipeline GitHub Actions: build, tests, Sonar, deploy.                |

---

## **7. Plan de trabajo con esfuerzo estimado**

| **Día**       | **Objetivo principal**           | **Entregables**                                                                                                                                                   | **Horas estimadas** |
| ------------- | -------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------- |
| **Sábado**    | Modelado completo de API y datos | • Contratos API<br>• DTOs request/response<br>• Validaciones y errores<br>• Modelo BD final                                                                       | **4h**              |
| **Domingo**   | Diagramas principales            | • C4 nivel 1, 2, 3<br>• Flujo JWT<br>• Flujo Checkpoints<br>• Flujo CI/CD                                                                                         | **4-5h**            |
| **Lunes**     | Skeleton de microservicios       | • Estructura hexagonal Java + Node.js<br>• Auth + Core levantan con Swagger<br>• Docker Compose funcionando<br>• Health checks básicos                            | **5h**              |
| **Martes**    | Casos de uso + NFRs              | • Implementar `POST /checkpoints` + `GET`<br>• Idempotencia + Circuit breakers<br>• Unitarias + integración con Testcontainers<br>• Métricas y logs estructurados | **6h**              |
| **Miércoles** | CI/CD y entrega final            | • GitHub Actions con build/test/sonar/deploy<br>• Documentación técnica final<br>• Validación contra requisitos del reto                                          | **3h**              |

**Total estimado:** **22-23 horas efectivas**

---

## **8. Próximo paso**

Mañana **sábado a las 6:00 a.m.** comenzamos con:

- Contratos API completos (payloads, responses, validaciones).
- Definir códigos de error y scopes JWT.
- Preparar documentación Swagger.
