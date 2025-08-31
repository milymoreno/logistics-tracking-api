---
title: Tracking System - Microservices Architecture
date: 30/08/2025
author: Mily Moreno
---

# Tracking System - Microservices Architecture

Sistema de tracking de envíos con arquitectura de microservicios.

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
├── docs/                      # Documentación y diagramas
└── scripts/                   # Scripts de deployment
```

## Servicios

### Auth Service (Node.js)
- **Puerto:** 3000
- **Responsabilidad:** Autenticación y emisión de JWT RS256
- **Endpoints:** `/auth/login`, `/.well-known/jwks.json`

### Core Service (Java/Quarkus)
- **Puerto:** 8080
- **Responsabilidad:** API de tracking y checkpoints
- **Endpoints:** `/api/v1/checkpoints`, `/api/v1/tracking/*`, `/api/v1/shipments`

## Tecnologías

- **Auth:** Node.js 22 + TypeScript + Fastify + JWT
- **Core:** Java 21 + Quarkus + Mutiny + PostgreSQL
- **Seguridad:** Vault + JWT RS256 + JWKS
- **Base de datos:** PostgreSQL 15
- **Contenedores:** Docker + Docker Compose