-- =====================================================
-- Script de creación de tablas para el sistema de tracking
-- Autor: Core Tracking Service
-- Fecha: 2025-01-09
-- Descripción: Tablas principales para el manejo de eventos de tracking
-- =====================================================

-- Crear tipo ENUM para los estados de tracking
CREATE TYPE tracking_status AS ENUM (
    'CREATED', 
    'IN_PROGRESS', 
    'PROCESSING', 
    'COMPLETED', 
    'FAILED', 
    'CANCELLED', 
    'PENDING', 
    'APPROVED', 
    'REJECTED'
);

-- Tabla principal de eventos de tracking
CREATE TABLE IF NOT EXISTS tracking_events (
    id BIGSERIAL PRIMARY KEY,
    tracking_id VARCHAR(50) NOT NULL,
    user_id VARCHAR(100) NOT NULL,
    status tracking_status NOT NULL,
    description TEXT NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices para optimizar consultas
CREATE INDEX IF NOT EXISTS idx_tracking_events_tracking_id ON tracking_events(tracking_id);
CREATE INDEX IF NOT EXISTS idx_tracking_events_user_id ON tracking_events(user_id);
CREATE INDEX IF NOT EXISTS idx_tracking_events_status ON tracking_events(status);
CREATE INDEX IF NOT EXISTS idx_tracking_events_timestamp ON tracking_events(timestamp DESC);
CREATE INDEX IF NOT EXISTS idx_tracking_events_created_at ON tracking_events(created_at DESC);

-- Índice compuesto para consultas frecuentes
CREATE INDEX IF NOT EXISTS idx_tracking_events_user_status ON tracking_events(user_id, status);
CREATE INDEX IF NOT EXISTS idx_tracking_events_tracking_timestamp ON tracking_events(tracking_id, timestamp DESC);

-- Función para actualizar el campo updated_at automáticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Trigger para actualizar updated_at en cada UPDATE
CREATE TRIGGER update_tracking_events_updated_at 
    BEFORE UPDATE ON tracking_events 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Comentarios en las tablas y columnas para documentación
COMMENT ON TABLE tracking_events IS 'Tabla principal que almacena todos los eventos de tracking del sistema';
COMMENT ON COLUMN tracking_events.id IS 'Identificador único autoincremental del evento';
COMMENT ON COLUMN tracking_events.tracking_id IS 'Identificador único del tracking (formato: TRK-XXXXXXXX)';
COMMENT ON COLUMN tracking_events.user_id IS 'Identificador del usuario que genera el evento';
COMMENT ON COLUMN tracking_events.status IS 'Estado actual del tracking';
COMMENT ON COLUMN tracking_events.description IS 'Descripción detallada del evento';
COMMENT ON COLUMN tracking_events.timestamp IS 'Fecha y hora cuando ocurrió el evento';
COMMENT ON COLUMN tracking_events.metadata IS 'Información adicional en formato JSON';
COMMENT ON COLUMN tracking_events.created_at IS 'Fecha y hora de creación del registro';
COMMENT ON COLUMN tracking_events.updated_at IS 'Fecha y hora de última actualización del registro';

-- =====================================================
-- DATOS DE PRUEBA PARA DESARROLLO
-- =====================================================

-- Insertar algunos eventos de prueba para testing
INSERT INTO tracking_events (tracking_id, user_id, status, description, timestamp, metadata) VALUES
('TRK-TEST001', 'user123', 'CREATED', 'Evento de prueba inicial', CURRENT_TIMESTAMP - INTERVAL '2 hours', '{"source": "test", "environment": "dev"}'),
('TRK-TEST001', 'user123', 'IN_PROGRESS', 'Proceso iniciado correctamente', CURRENT_TIMESTAMP - INTERVAL '1 hour', '{"source": "test", "step": 2}'),
('TRK-TEST001', 'user123', 'COMPLETED', 'Proceso completado exitosamente', CURRENT_TIMESTAMP - INTERVAL '30 minutes', '{"source": "test", "step": 3, "duration": "90min"}'),

('TRK-TEST002', 'user456', 'CREATED', 'Segundo evento de prueba', CURRENT_TIMESTAMP - INTERVAL '1 hour', '{"source": "api", "environment": "dev"}'),
('TRK-TEST002', 'user456', 'PROCESSING', 'Procesando datos del usuario', CURRENT_TIMESTAMP - INTERVAL '45 minutes', '{"source": "api", "step": 2}'),
('TRK-TEST002', 'user456', 'FAILED', 'Error en el procesamiento', CURRENT_TIMESTAMP - INTERVAL '30 minutes', '{"source": "api", "error": "timeout", "step": 2}'),

('TRK-TEST003', 'user789', 'CREATED', 'Tercer evento de prueba', CURRENT_TIMESTAMP - INTERVAL '30 minutes', '{"source": "web", "environment": "dev"}'),
('TRK-TEST003', 'user789', 'PENDING', 'Esperando aprobación', CURRENT_TIMESTAMP - INTERVAL '15 minutes', '{"source": "web", "step": 2, "approver": "admin"}'),

('TRK-TEST004', 'user123', 'CREATED', 'Cuarto evento para el mismo usuario', CURRENT_TIMESTAMP - INTERVAL '10 minutes', '{"source": "mobile", "environment": "dev"}'),
('TRK-TEST004', 'user123', 'IN_PROGRESS', 'Procesando en dispositivo móvil', CURRENT_TIMESTAMP - INTERVAL '5 minutes', '{"source": "mobile", "device": "android", "step": 2}');

-- Mostrar estadísticas de los datos insertados
DO $$
BEGIN
    RAISE NOTICE 'Base de datos inicializada correctamente';
    RAISE NOTICE 'Eventos de tracking creados: %', (SELECT COUNT(*) FROM tracking_events);
    RAISE NOTICE 'Tracking IDs únicos: %', (SELECT COUNT(DISTINCT tracking_id) FROM tracking_events);
    RAISE NOTICE 'Usuarios únicos: %', (SELECT COUNT(DISTINCT user_id) FROM tracking_events);
END $$;