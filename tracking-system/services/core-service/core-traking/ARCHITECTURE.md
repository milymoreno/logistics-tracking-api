# Arquitectura del Sistema P2M

## Resumen Ejecutivo

Este repositorio implementa una **arquitectura de microservicios** basada en **Clean Architecture** y **Hexagonal Architecture**, diseñada para el sistema P2M (Peer-to-Peer Money) de BT. La solución está compuesta por tres microservicios independientes que manejan diferentes aspectos del negocio.

## Estructura General del Repositorio

```
├── .vsts/                    # Configuración Azure DevOps
├── pipeline/                 # Pipelines de CI/CD compartidos
├── services/                 # Microservicios
│   ├── compensation/         # Servicio de compensación bancaria
│   ├── directory/           # Servicio de directorio de usuarios
│   └── transaction/         # Servicio de transacciones
├── .gitignore
└── README.md
```

## Microservicios

### 1. Compensation Service
**Propósito**: Manejo de compensaciones bancarias y generación de reportes financieros.

### 2. Directory Service  
**Propósito**: Gestión del directorio de usuarios y entidades del sistema.

### 3. Transaction Service
**Propósito**: Procesamiento y gestión de transacciones peer-to-peer.

## Clean Architecture por Servicio

Cada microservicio implementa Clean Architecture con tres capas principales:

### 📁 Application Layer (`application/`)
**Responsabilidad**: Orquestación de casos de uso y configuración de la aplicación.

```
application/
├── configuration/           # Configuración de Spring Boot y beans
├── decorator/              # Decoradores para manejo de excepciones
└── handler/               # Manejadores de casos de uso específicos
    └── impl/              # Implementaciones concretas
```

**Componentes clave**:
- `Configuration.java` - Configuración principal de Spring Boot
- `ExceptionStatus.java` - Decorador para manejo de estados de excepción
- `AllReportsHandler.java` - Manejador para generación de reportes

### 📁 Domain Layer (`domain/`)
**Responsabilidad**: Lógica de negocio pura, independiente de frameworks externos.

```
domain/
├── abstracfactory/         # Abstract Factory pattern para reportes
│   ├── formats/           # Fábricas de formatos (Excel, CSV, etc.)
│   ├── impl/              # Implementaciones concretas
│   └── reports/           # Fábricas de tipos de reportes
├── builder/               # Builder pattern para construcción de objetos
├── dtos/                  # Data Transfer Objects
│   └── formats/           # DTOs específicos por formato
├── enums/                 # Enumeraciones del dominio
├── exception/             # Excepciones personalizadas del dominio
├── model/                 # Modelos del dominio
│   └── appconfig/         # Modelos de configuración
├── ports/                 # Interfaces (puertos) para adaptadores
│   └── formats/           # Puertos específicos por formato
├── query/                 # Lógica de consultas con Strategy pattern
│   ├── context/           # Contextos de consulta
│   ├── impl/              # Implementaciones de estrategias
│   ├── strategy/          # Interfaces de estrategias
│   └── utils/             # Utilidades de consulta
├── request/               # Objetos de request del dominio
├── service/               # Servicios del dominio
│   ├── impl/              # Implementaciones de servicios
│   └── reports/           # Servicios específicos de reportes
└── utils/                 # Utilidades del dominio
```

**Componentes clave**:
- **Enums**: `EBanks`, `EFormats`, `EReportType`, `EFileNames`, `EProperties`
- **DTOs**: `BankDTO`, `ReportDTO`, `QueryDTO`, `EncryptorDTO`
- **Exceptions**: `CustomException`, `FileGenerationException`, `ErrorResponse`
- **Ports**: `BankConsumer`, `EncryptorRepository`, `QueryRepository`, `AzureFunctionConsumer`
- **Services**: `AllReportsService`
- **Utils**: `DateTimeUtil`, `FileNameUtil`, `MeridianTypeValidator`

### 📁 Infrastructure Layer (`infrastructure/`)
**Responsabilidad**: Implementación de adaptadores externos y puntos de entrada.

```
infrastructure/
├── drivenadapters/         # Adaptadores para servicios externos
│   ├── encryptor/         # Adaptador para servicio de encriptación
│   ├── formats/           # Adaptadores para diferentes formatos
│   ├── mssql/             # Adaptador para SQL Server
│   ├── parameter/         # Adaptador para manejo de parámetros
│   └── restclient/        # Clientes REST para servicios externos
├── entrypoints/           # Puntos de entrada a la aplicación
│   ├── controller/        # Controladores REST
│   └── task/              # Tareas programadas y jobs
└── utils/                 # Utilidades de infraestructura
    └── response/          # Utilidades para manejo de respuestas
```

**Componentes clave**:
- **Controllers**: Endpoints REST para exposición de servicios
- **Tasks**: Jobs programados para procesamiento batch
- **Driven Adapters**: Implementaciones de puertos para servicios externos
- **Utils**: `ClientExceptionMapper` para mapeo de excepciones

## Patrones de Diseño Implementados

### 1. Clean Architecture
- **Separación clara de responsabilidades** en capas
- **Inversión de dependencias** mediante puertos y adaptadores
- **Independencia de frameworks** en la capa de dominio

### 2. Hexagonal Architecture (Ports & Adapters)
- **Puertos** definidos en el dominio como interfaces
- **Adaptadores** implementados en infraestructura
- **Aislamiento** del core de negocio

### 3. Abstract Factory Pattern
- **Creación de familias de objetos** relacionados (reportes por formato)
- **Extensibilidad** para nuevos formatos sin modificar código existente
- **Encapsulación** de la lógica de creación

### 4. Builder Pattern
- **Construcción paso a paso** de objetos complejos (reportes)
- **Flexibilidad** en la configuración de objetos
- **Inmutabilidad** de objetos construidos

### 5. Strategy Pattern
- **Diferentes algoritmos** para consultas de datos
- **Intercambiabilidad** de estrategias en tiempo de ejecución
- **Extensibilidad** para nuevas estrategias de consulta

### 6. Dependency Injection
- **Inversión de control** mediante Spring Boot
- **Testabilidad** mejorada
- **Bajo acoplamiento** entre componentes

## Tecnologías y Frameworks

### Backend
- **Java** - Lenguaje principal
- **Spring Boot** - Framework de aplicación
- **Maven** - Gestión de dependencias

### Base de Datos
- **Microsoft SQL Server** - Base de datos principal
- **JDBC** - Conectividad a base de datos

### Integración
- **Azure Functions** - Servicios serverless
- **REST APIs** - Comunicación entre servicios
- **Azure DevOps** - CI/CD y gestión de código

### Seguridad
- **Encriptación** de datos sensibles
- **Validación** de parámetros de entrada
- **Manejo seguro** de secretos

## Características Arquitectónicas

### Escalabilidad
- **Microservicios independientes** que pueden escalarse por separado
- **Separación de responsabilidades** por dominio de negocio
- **Stateless services** para mejor escalabilidad horizontal

### Mantenibilidad
- **Clean Architecture** facilita cambios y evolución
- **Separación clara de capas** reduce el acoplamiento
- **Patrones de diseño** bien definidos

### Testabilidad
- **Inversión de dependencias** permite fácil mocking
- **Separación de lógica de negocio** de infraestructura
- **Interfaces bien definidas** para testing

### Extensibilidad
- **Abstract Factory** para nuevos formatos de reporte
- **Strategy Pattern** para nuevas estrategias de consulta
- **Ports & Adapters** para nuevas integraciones

## Flujo de Datos Típico

1. **Request** llega al **Controller** (Infrastructure/Entrypoints)
2. **Controller** invoca **Handler** (Application)
3. **Handler** orquesta **Services** del dominio (Domain)
4. **Services** utilizan **Ports** para acceder a datos (Domain)
5. **Adapters** implementan los puertos (Infrastructure/DrivenAdapters)
6. **Response** se construye y retorna al cliente

## Convenciones de Código

### Nomenclatura
- **Packages**: lowercase con separación por puntos
- **Classes**: PascalCase con sufijos descriptivos (Service, Repository, etc.)
- **Methods**: camelCase con verbos descriptivos
- **Constants**: UPPER_SNAKE_CASE en enums

### Organización
- **Una clase por archivo** con nombre coincidente
- **Interfaces** en el dominio, implementaciones en infraestructura
- **DTOs** separados por contexto de uso
- **Exceptions** específicas por tipo de error

## Próximos Pasos Recomendados

1. **Documentación de APIs** con OpenAPI/Swagger
2. **Testing automatizado** con cobertura completa
3. **Monitoreo y observabilidad** con métricas de negocio
4. **Containerización** con Docker para deployment
5. **Service mesh** para comunicación entre microservicios

---

*Documento generado automáticamente - Última actualización: Septiembre 2025*