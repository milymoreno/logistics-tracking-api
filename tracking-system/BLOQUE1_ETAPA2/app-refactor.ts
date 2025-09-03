// BLOQUE1_ETAPA2/app-refactor.ts
// Refactor del ejemplo original aplicando SOLID, Clean Code, patrones y arquitectura limpia
// Comentarios explican cada mejora y principio aplicado

import Fastify, { FastifyInstance, FastifyReply, FastifyRequest } from "fastify";

// 1. Definición de tipos y contratos (evitar any, tipado fuerte)
interface Checkpoint {
  id: string;
  unitId: string;
  status: string;
  timestamp: string;
}

interface Unit {
  id: string;
  status: string;
  checkpoints: Array<{ status: string; date: string }>;
}

// 2. Repositorios: Separar persistencia (Single Responsibility, Clean Architecture)
interface ICheckpointRepository {
  create(checkpoint: Checkpoint): void;
  findByUnitId(unitId: string): Checkpoint[];
}

class InMemoryCheckpointRepository implements ICheckpointRepository {
  private checkpoints: Checkpoint[] = [];
  create(checkpoint: Checkpoint) {
    // Idempotencia: evitar duplicados por unitId+status+timestamp
    if (!this.checkpoints.find(c => c.unitId === checkpoint.unitId && c.status === checkpoint.status && c.timestamp === checkpoint.timestamp)) {
      this.checkpoints.push(checkpoint);
    }
  }
  findByUnitId(unitId: string) {
    return this.checkpoints.filter(c => c.unitId === unitId);
  }
}

interface IUnitRepository {
  findById(unitId: string): Unit | undefined;
  save(unit: Unit): void;
  findByStatus(status: string): Unit[];
}

class InMemoryUnitRepository implements IUnitRepository {
  private units: Unit[] = [];
  findById(unitId: string) {
    return this.units.find(u => u.id === unitId);
  }
  save(unit: Unit) {
    const idx = this.units.findIndex(u => u.id === unit.id);
    if (idx >= 0) this.units[idx] = unit;
    else this.units.push(unit);
  }
  findByStatus(status: string) {
    return this.units.filter(u => u.status === status);
  }
}

// 3. Servicios de dominio: lógica pura, sin dependencias de framework (SRP, Open/Closed)
class CheckpointService {
  constructor(private repo: ICheckpointRepository) {}
  createCheckpoint(unitId: string, status: string, timestamp: Date) {
    // Validación de entrada
    if (!unitId || !status) throw new Error("unitId y status requeridos");
    const checkpoint: Checkpoint = {
      id: crypto.randomUUID(),
      unitId,
      status,
      timestamp: timestamp.toISOString(),
    };
    this.repo.create(checkpoint);
    return checkpoint;
  }
  getHistory(unitId: string) {
    if (!unitId) throw new Error("unitId requerido");
    return this.repo.findByUnitId(unitId);
  }
}

class UnitService {
  constructor(private repo: IUnitRepository) {}
  updateUnitStatus(unitId: string, newStatus: string) {
    if (!unitId || !newStatus) throw new Error("unitId y status requeridos");
    let unit = this.repo.findById(unitId);
    if (!unit) {
      unit = { id: unitId, status: newStatus, checkpoints: [] };
    }
    unit.status = newStatus;
    unit.checkpoints.push({ status: newStatus, date: new Date().toISOString() });
    this.repo.save(unit);
    return unit;
  }
  getUnitsByStatus(status: string) {
    if (!status) throw new Error("status requerido");
    return this.repo.findByStatus(status);
  }
}

// 4. Controladores: sólo orquestan, no lógica de negocio (SRP, Clean Architecture)
class TrackingController {
  constructor(
    private checkpointService: CheckpointService,
    private unitService: UnitService
  ) {}

  async createCheckpoint(req: FastifyRequest, reply: FastifyReply) {
    try {
      const { unitId, status } = req.body as any;
      // Validación de entrada
      if (!unitId || !status) return reply.status(400).send({ error: "unitId y status requeridos" });
      const checkpoint = this.checkpointService.createCheckpoint(unitId, status, new Date());
      this.unitService.updateUnitStatus(unitId, status);
      return reply.status(201).send(checkpoint);
    } catch (err: any) {
      return reply.status(400).send({ error: err.message });
    }
  }

  async getHistory(req: FastifyRequest, reply: FastifyReply) {
    try {
      const { unitId } = req.query as any;
      if (!unitId) return reply.status(400).send({ error: "unitId requerido" });
      const history = this.checkpointService.getHistory(unitId);
      return reply.send(history);
    } catch (err: any) {
      return reply.status(400).send({ error: err.message });
    }
  }

  async getUnitsByStatus(req: FastifyRequest, reply: FastifyReply) {
    try {
      const { status } = req.query as any;
      if (!status) return reply.status(400).send({ error: "status requerido" });
      const units = this.unitService.getUnitsByStatus(status);
      return reply.send(units);
    } catch (err: any) {
      return reply.status(400).send({ error: err.message });
    }
  }
}

// 5. Inyección de dependencias manual (puede usarse un contenedor DI en proyectos grandes)
const checkpointRepo = new InMemoryCheckpointRepository();
const unitRepo = new InMemoryUnitRepository();
const checkpointService = new CheckpointService(checkpointRepo);
const unitService = new UnitService(unitRepo);
const controller = new TrackingController(checkpointService, unitService);

// 6. Configuración de Fastify y rutas (sólo orquestación, sin lógica)
const app: FastifyInstance = Fastify();

app.post("/checkpoint", controller.createCheckpoint.bind(controller));
app.get("/history", controller.getHistory.bind(controller));
app.get("/unitsByStatus", controller.getUnitsByStatus.bind(controller));

app.setErrorHandler((error, req, reply) => {
  // Manejo global de errores
  reply.status(500).send({ error: error.message });
});

app.listen({ port: 3000 }, (err, address) => {
  if (err) {
    process.exit(1);
  }
  console.log(`Server running at ${address}`);
});

// Comentarios de refactor:
// - Separa responsabilidades (SRP, Clean Architecture)
// - Usa interfaces y tipado fuerte (TypeScript, Clean Code)
// - Inyección de dependencias (DI)
// - Validación de entrada y manejo de errores HTTP
// - Idempotencia en creación de checkpoints
// - Controladores sin lógica de negocio
// - Repositorios desacoplados (pueden ser reemplazados por persistencia real)
// - Código extensible y testeable
