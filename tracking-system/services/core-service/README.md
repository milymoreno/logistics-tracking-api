# Core Service - Quarkus Reactive

Microservicio principal para el manejo de checkpoints y tracking de envíos.

## Tecnologías
- Java 21
- Quarkus 3.x
- Mutiny (Reactive Streams)
- PostgreSQL Reactive Client
- SmallRye JWT
- SmallRye Fault Tolerance

## Arquitectura
- Hexagonal Architecture
- CQRS (Command Query Responsibility Segregation)
- Domain Driven Design (DDD)

## Endpoints
- `POST /api/v1/checkpoints` - Crear checkpoint
- `GET /api/v1/tracking/{shipmentId}` - Obtener tracking
- `GET /api/v1/shipments` - Listar envíos

## Desarrollo
```bash
./mvnw quarkus:dev
```

## Testing
```bash
./mvnw test
```