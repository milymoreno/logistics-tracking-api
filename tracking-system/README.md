---
title: Tracking System - Microservices Architecture
date: 30/08/2025
author: Mily Moreno
---

# Tracking System - Microservices Architecture


Sistema de tracking de envíos con arquitectura de microservicios, orientado a escalabilidad, resiliencia y buenas prácticas de ingeniería moderna.


## Estructura del Proyecto

```
tracking-system/
├── services/
│   ├── auth-service/          # Node.js + TypeScript + Fastify
│   └── core-service/          # Java 21 + Quarkus + Mutiny
├── infrastructure/
│   ├── docker-compose.yml     # Orquestación de servicios
│   ├── vault/                 # Configuración de Vault
│   └── postgres/              # Scripts de BD
├── C4/                        # Diagramas de arquitectura C4
├── ARQUITECTURA_Y_EJECUCION_LOCAL.md # Documentación técnica y de operación
└── README.md                  # Este archivo
```


## Servicios Principales

### Auth Service (Node.js)
- **Puerto:** 3000
- **Responsabilidad:** Autenticación, autorización y emisión de JWT RS256 (con claves gestionadas en Vault)
- **Endpoints:** `/auth/login`, `/.well-known/jwks.json`, `/health`

### Core Service (Java/Quarkus)
- **Puerto:** 8081
- **Responsabilidad:** API de tracking/logística, gestión de eventos y checkpoints
- **Endpoints:** `/api/v1/checkpoints`, `/api/v1/tracking/*`, `/api/v1/shipments`, `/api/v1/health`


## Tecnologías y Arquitectura

- **Auth:** Node.js 22, TypeScript, Fastify, JWT RS256, integración con Vault
- **Core:** Java 21, Quarkus 3.x, Mutiny (reactivo), PostgreSQL, SmallRye JWT
- **Seguridad:** Vault (gestión de secretos), JWT RS256, JWKS
- **Contenedores:** Docker, Docker Compose, preparado para Kubernetes
- **Calidad:** SOLID, Clean Architecture, pruebas unitarias/integración, observabilidad

## Ejecución Local Rápida

1. Instala Docker, Node.js 22 y Java 17/21.
2. Levanta infraestructura:
	```bash
	cd infrastructure
	docker compose up -d
	```
3. Inicia los servicios:
	```bash
	# Core (Quarkus)
	cd ../services/core-service/bt-p2m-back
	./mvnw quarkus:dev
	# Auth
	cd ../../auth-service
	npm install
	npm run dev
	```
4. Consulta endpoints de salud:
	- Tracking: http://localhost:8081/api/v1/health
	- Auth: http://localhost:3000/health

Más detalles y fundamentos en `ARQUITECTURA_Y_EJECUCION_LOCAL.md`.

## Documentación y Referencias

- [ARQUITECTURA_Y_EJECUCION_LOCAL.md](./ARQUITECTURA_Y_EJECUCION_LOCAL.md): Documentación completa de arquitectura, decisiones técnicas, operación y mejores prácticas.
- Diagramas C4 en la carpeta `/C4`.
- OpenAPI/Swagger disponible en `/swagger-ui` de cada servicio.

---
Este README resume la visión global. Consulta el MD de arquitectura para detalles técnicos, fundamentos y operación avanzada.