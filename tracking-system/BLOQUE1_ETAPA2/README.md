# Bloque 1 - Etapa 2: Revisión y Refactor de Código

## Objetivo
Identificar al menos 15 errores principales en el código de ejemplo, proponer soluciones y refactorizar aplicando SOLID, Clean Code, patrones de diseño y arquitectura limpia.

## Entregables
- Código original (para referencia)
- Lista de problemas detectados, con principio afectado y riesgo asociado
- Código refactorizado con separación de capas, validaciones, DI, manejo de errores, etc.

---

## 1. Código Original

```typescript
// app.ts
import Fastify from "fastify";

class CheckpointManager {
    checkpoints: any[] = [];

    createCheckpoint(unitId: string, status: string, timestamp: Date) {
        this.checkpoints.push({
            id: Math.random().toString(),
            unitId,
            status,
            timestamp: timestamp.toISOString(),
            history: []
        });
        return this.checkpoints;
    }

    getHistory(unitId: string) {
        return this.checkpoints.filter(c => c.unitId == unitId);
    }
}

class UnitStatusService {
    units: any[] = [];

    updateUnitStatus(unitId: string, newStatus: string) {
        let unit = this.units.find(u => u.id == unitId);
        if (!unit) {
            unit = { id: unitId, status: newStatus, checkpoints: [] };
            this.units.push(unit);
        }
        unit.status = newStatus;
         unit.checkpoints.push({  status:  newStatus,  date:  new Date().toString() });
        return unit;
    }

    getUnitsByStatus(status: string) {
        return this.units.filter(u => u.status == status);
    }
}

class TrackingAPI {
    checkpointManager = new CheckpointManager();
    unitService = new UnitStatusService();

    registerRoutes(app: any) {
        app.post("/checkpoint", async (req: any, reply: any) => {
            const { unitId, status } = req.body;
             const cp = this.checkpointManager.createCheckpoint(unitId, status, new Date());
            this.unitService.updateUnitStatus(unitId, status);
            reply.send(cp);
        });

        app.get("/history", async (req: any, reply: any) => {
            const { unitId } = req.query as any;
            reply.send(this.checkpointManager.getHistory(unitId));
        });

        app.get("/unitsByStatus", async (req: any, reply: any) => {
            const { status } = req.query as any;
            reply.send(this.unitService.getUnitsByStatus(status));
        });
    }
}

const app = Fastify();
const api = new TrackingAPI();

api.registerRoutes(app);

app.listen({ port: 3000 }, (err: any, address: string) => {
    if (err) {
        process.exit(1);
    }
    console.log(`Server running at ${address}`);
});
```

---

## 2. Lista de Problemas Detectados


### Problemas identificados y corregidos

1. **Uso de `any` y falta de tipado fuerte**  
   - Principio: Clean Code, Type Safety  
   - Riesgo: Errores en tiempo de ejecución, difícil mantenimiento.
2. **Persistencia acoplada a la lógica de negocio**  
   - Principio: SRP, Clean Architecture  
   - Riesgo: Difícil cambiar la fuente de datos, bajo testeo.
3. **No hay separación de capas (controlador, servicio, repositorio)**  
   - Principio: SRP, Clean Architecture  
   - Riesgo: Código monolítico, difícil de escalar y mantener.
4. **No hay interfaces/contratos para dependencias**  
   - Principio: Dependency Inversion (SOLID)  
   - Riesgo: No se puede cambiar la implementación sin modificar consumidores.
5. **No hay inyección de dependencias**  
   - Principio: Dependency Injection (SOLID)  
   - Riesgo: Acoplamiento fuerte, difícil de testear.
6. **No hay validación de datos de entrada**  
   - Principio: Defensive Programming, API Design  
   - Riesgo: Datos corruptos, errores inesperados, seguridad.
7. **No hay manejo de errores ni respuestas HTTP claras**  
   - Principio: Robustness, API Design  
   - Riesgo: Difícil de depurar, mala experiencia de usuario.
8. **No hay idempotencia en la creación de checkpoints**  
   - Principio: Idempotency, Consistency  
   - Riesgo: Duplicidad de datos, inconsistencias.
9. **Controladores mezclan lógica de negocio y orquestación**  
   - Principio: SRP, Clean Architecture  
   - Riesgo: Difícil de mantener y extender.
10. **No hay separación entre dominio y framework**  
    - Principio: Clean Architecture  
    - Riesgo: Difícil migrar de framework, bajo reuso.
11. **No hay manejo global de errores**  
    - Principio: Robustness  
    - Riesgo: Fallos silenciosos, respuestas inconsistentes.
12. **No hay validación de parámetros obligatorios**  
    - Principio: API Design  
    - Riesgo: Requests inválidos procesados.
13. **No hay generación segura de IDs**  
    - Principio: Security, Consistency  
    - Riesgo: Colisiones, inseguridad.
14. **No hay tests ni estructura para testear**  
    - Principio: Testability  
    - Riesgo: Cambios inseguros, bugs ocultos.
15. **No hay comentarios ni documentación en el código**  
    - Principio: Clean Code  
    - Riesgo: Difícil de entender y mantener.
16. **No hay extensibilidad ni posibilidad de cambiar persistencia**  
    - Principio: Open/Closed, Dependency Inversion  
    - Riesgo: Refactors costosos.
17. **No hay manejo de fechas consistente (uso de toString())**  
    - Principio: Consistency  
    - Riesgo: Formatos inconsistentes, errores de parsing.
18. **No hay control de errores en operaciones de arrays**  
    - Principio: Defensive Programming  
    - Riesgo: Excepciones no controladas.
19. **No hay separación de responsabilidades en servicios**  
    - Principio: SRP  
    - Riesgo: Servicios con demasiadas funciones.
20. **No hay validación de unicidad en checkpoints**  
    - Principio: Consistency, Idempotency  
    - Riesgo: Duplicidad de eventos.

---

## 3. Código Refactorizado

(Agregar aquí la versión refactorizada, separando capas, aplicando DI, validaciones, manejo de errores, etc.)

---

## 4. Explicación de Decisiones y Mejoras

(Justificar los cambios, cómo se aplican SOLID, Clean Code, patrones, etc.)

---

## 5. (Opcional) Tests y Casos de Uso