---
title: Documentación de Arquitectura y Decisiones Técnicas
author: Mildred María Moreno Liscano
date: 02-09-2025
---

# Documentación de Arquitectura y Decisiones Técnicas

## 1. Introducción
Este documento describe la arquitectura, decisiones técnicas y fundamentos de calidad aplicados en el sistema de tracking logístico. Se justifica cada elección tecnológica y de diseño, alineando con principios de microservicios, escalabilidad, resiliencia y buenas prácticas de ingeniería de software.

> **Nota:** Se planeó agregar CI/CD con GitHub Actions, pero no fue posible implementarlo en esta entrega. La aplicación está diseñada para ejecutarse en contenedores (Docker) y puede ser orquestada fácilmente con Kubernetes para entornos de mayor escala y producción. Esto permite portabilidad, despliegue automatizado y escalabilidad horizontal en infraestructuras modernas.

### Consideraciones adicionales sobre CI/CD y Contenedores

- **Preparación para CI/CD:** El proyecto está estructurado para facilitar la integración de pipelines de CI/CD en el futuro (por ejemplo, con GitHub Actions, GitLab CI o Jenkins). Se recomienda automatizar pruebas, builds y despliegues para ambientes de desarrollo, staging y producción.
- **Contenedores y Kubernetes:** Todos los servicios están listos para ser empaquetados como imágenes Docker. Esto permite su despliegue en cualquier plataforma compatible con contenedores. Para ambientes empresariales o de alta disponibilidad, se recomienda usar Kubernetes (K8s) para orquestar, escalar y gestionar los servicios, aprovechando características como autoescalado, rolling updates y tolerancia a fallos.
- **Recomendaciones para producción:**
  - Configurar monitoreo y logging centralizado (por ejemplo, Prometheus, Grafana, ELK).
  - Usar almacenamiento persistente para bases de datos.
  - Gestionar secretos y variables sensibles exclusivamente con Vault u otro gestor seguro.
  - Definir recursos y límites adecuados para cada contenedor en Kubernetes.

## 2. Arquitectura General
El sistema está compuesto por microservicios independientes (Java/Quarkus y Node.js), cada uno con su propia base de datos y responsabilidades bien definidas. Se utiliza Docker Compose para orquestar los servicios y Vault para la gestión segura de secretos.

### Diagrama de Componentes
- **auth-service** (Node.js/Fastify)
- **core-service/tracking-service** (Java/Quarkus)

# Documentación de Arquitectura y Decisiones Técnicas

## 1. Introducción
Este documento describe la arquitectura, decisiones técnicas y fundamentos de calidad aplicados en el sistema de tracking logístico. Se justifica cada elección tecnológica y de diseño, alineando con principios de microservicios, escalabilidad, resiliencia y buenas prácticas de ingeniería de software.

## 2. Arquitectura General
El sistema está compuesto por microservicios independientes (Java/Quarkus y Node.js), cada uno con su propia base de datos y responsabilidades bien definidas. Se utiliza Docker Compose para orquestar los servicios y Vault para la gestión segura de secretos.

### Diagrama de Componentes
- **auth-service** (Node.js/Fastify)
- **core-service/tracking-service** (Java/Quarkus)
- **PostgreSQL** (instancias separadas por servicio)
- **Vault** (gestión de secretos)

## 3. Decisiones Fundamentales

### 3.1. Microservicios y Bases de Datos Separadas
- **Justificación:** Cada microservicio es autónomo, con su propia base de datos, siguiendo el principio de independencia y desacoplamiento. Esto permite escalar, desplegar y evolucionar cada servicio de forma aislada, evitando cuellos de botella y dependencias fuertes.
- **Referencia:** [Microservices Database Patterns](https://martinfowler.com/articles/microservices.html#DatabasesPerService)

### 3.2. Programación Reactiva (Quarkus/Mutiny)
- **Justificación:** Se eligió programación reactiva en el core-service para soportar alta concurrencia y eficiencia en el uso de recursos, mejorando la escalabilidad y la capacidad de respuesta bajo carga. Mutiny y Quarkus permiten manejar flujos asíncronos y no bloqueantes, ideales para sistemas modernos y distribuidos.
- **Beneficios:**
  - Mejor uso de hilos y recursos.
  - Resiliencia ante picos de tráfico.
  - Menor latencia en operaciones I/O.

### 3.3. Node.js en auth-service
- **Justificación:** Node.js es eficiente para servicios de autenticación por su modelo asíncrono y rápido time-to-market. Permite manejar muchas conexiones concurrentes y facilita la integración con JWT y Vault.
- **Ventajas:**
  - Ecosistema maduro para autenticación y APIs REST.
  - Facilidad de integración con herramientas modernas.

### 3.4. Uso de Vault
- **Justificación:** Vault centraliza y protege secretos (tokens, claves, contraseñas), evitando exponer información sensible en el código o variables de entorno. Mejora la seguridad y el cumplimiento de buenas prácticas.
- **Ventajas:**
  - Rotación automática de secretos.
  - Acceso controlado y auditado.

### 3.5. JWT y Seguridad
- **Justificación:** JWT (RS256) permite autenticación y autorización segura y desacoplada entre servicios. El uso de JWKS facilita la validación de tokens sin compartir claves privadas.
- **Ventajas:**
  - Escalabilidad horizontal (stateless).
  - Interoperabilidad entre servicios.

### 3.6. Docker y Docker Compose
- **Justificación:** Facilita la portabilidad, despliegue y replicación del entorno local a producción. Permite levantar todo el stack con un solo comando, asegurando consistencia.

### 3.7. Calidad de Código y Arquitectura
- **Principios Aplicados:**
  - **SOLID:** Cada clase/módulo tiene una única responsabilidad, fácil de extender y mantener.
  - **Clean Architecture:** Separación clara entre dominio, infraestructura y presentación.
  - **DRY/KISS:** Código simple, reutilizable y sin duplicaciones.
  - **Testing:** Pruebas unitarias y de integración para asegurar confiabilidad.

### 3.8. Escalabilidad y Resiliencia
- **Escalabilidad:**
  - Servicios desacoplados, escalables horizontalmente.
  - Bases de datos independientes para evitar cuellos de botella.
- **Resiliencia:**
  - Manejo de errores y timeouts.
  - Health checks y readiness probes.
  - Uso de patrones como Circuit Breaker (potencial).

## 4. Ejecución Local

### 4.1. Requisitos Previos
- Docker y Docker Compose
- Java 17/21 y Maven (para desarrollo core-service)
- Node.js 22 y npm (para desarrollo auth-service)

### 4.2. Levantar Todo el Sistema
```bash
cd infrastructure
# Levanta bases de datos y Vault
sudo docker compose up -d
cd ../services/core-service/bt-p2m-back
# Levanta tracking-service
./mvnw quarkus:dev
cd ../../auth-service
# Instala dependencias y levanta auth-service
npm install
npm run dev
```

### 4.3. Variables de Entorno y Secretos
- Revisar los archivos `.env` y `application.properties` para configurar las conexiones locales.
- Vault debe estar inicializado y los secretos cargados según los scripts de `infrastructure/vault/init.sh`.

### 4.4. Endpoints de Salud
- **Tracking:** http://localhost:8081/api/v1/health
- **Auth:** http://localhost:3000/health

## 5. Referencias y Buenas Prácticas
- [Microservices Patterns - Chris Richardson](https://microservices.io/patterns/index.html)
- [Clean Architecture - Uncle Bob](https://8thlight.com/blog/uncle-bob/2012/08/13/the-clean-architecture.html)
- [SOLID Principles](https://en.wikipedia.org/wiki/SOLID)
- [Quarkus Reactive](https://quarkus.io/guides/getting-started-reactive)
- [Vault Best Practices](https://developer.hashicorp.com/vault/docs/best-practices)

## 6. Observabilidad, Documentación y Cultura DevOps

- **Observabilidad:** Se recomienda implementar monitoreo de métricas, trazas y logs para todos los servicios. Herramientas como Prometheus, Grafana, Jaeger y ELK Stack permiten detectar cuellos de botella, errores y analizar el comportamiento del sistema en tiempo real.
- **Documentación:** Mantener actualizada la documentación técnica y de usuario es clave para la mantenibilidad y la transferencia de conocimiento. Se sugiere el uso de OpenAPI/Swagger para documentar los endpoints REST y diagramas C4 para visualizar la arquitectura.
- **Cultura DevOps:** Fomentar la colaboración entre desarrollo y operaciones, automatizando pruebas, despliegues y monitoreo. Adoptar integración y entrega continua (CI/CD) reduce errores humanos y acelera la entrega de valor.

---

Este documento debe acompañar al código fuente y actualizarse ante cualquier cambio relevante en la arquitectura o decisiones técnicas.
