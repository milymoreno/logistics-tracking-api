-- Database initialization script for Tracking System
-- Create extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
-- States catalog
CREATE TABLE states (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    is_final BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- Insert default states
INSERT INTO states (code, name, description, is_final)
VALUES (
        'CREATED',
        'Creado',
        'Guía creada en el sistema',
        FALSE
    ),
    (
        'PICKED_UP',
        'Recolectado',
        'Paquete recolectado',
        FALSE
    ),
    (
        'IN_TRANSIT',
        'En tránsito',
        'Paquete en movimiento',
        FALSE
    ),
    (
        'OUT_FOR_DELIVERY',
        'En reparto',
        'Paquete en reparto final',
        FALSE
    ),
    (
        'DELIVERED',
        'Entregado',
        'Paquete entregado exitosamente',
        TRUE
    ),
    (
        'FAILED_DELIVERY',
        'Entrega fallida',
        'Intento de entrega fallido',
        FALSE
    ),
    (
        'RETURNED',
        'Devuelto',
        'Paquete devuelto al remitente',
        TRUE
    ),
    ('LOST', 'Perdido', 'Paquete perdido', TRUE);
-- Shipments (guías)
CREATE TABLE shipments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    shipment_id VARCHAR(50) UNIQUE NOT NULL,
    sender_name VARCHAR(100) NOT NULL,
    sender_address TEXT NOT NULL,
    recipient_name VARCHAR(100) NOT NULL,
    recipient_address TEXT NOT NULL,
    total_units INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- Shipment units (unidades de la guía)
CREATE TABLE shipment_units (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    shipment_id UUID NOT NULL REFERENCES shipments(id),
    unit_number INTEGER NOT NULL,
    barcode VARCHAR(100) UNIQUE NOT NULL,
    weight_kg DECIMAL(10, 2),
    dimensions_cm VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(shipment_id, unit_number)
);
-- Unit checkpoint log (bitácora de checkpoints por unidad)
CREATE TABLE unit_checkpoint_log (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    unit_id UUID NOT NULL REFERENCES shipment_units(id),
    state_id INTEGER NOT NULL REFERENCES states(id),
    location VARCHAR(200),
    notes TEXT,
    checkpoint_timestamp TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100)
);
-- Current state per unit (estado actual por unidad)
CREATE TABLE unit_current_state (
    unit_id UUID PRIMARY KEY REFERENCES shipment_units(id),
    state_id INTEGER NOT NULL REFERENCES states(id),
    location VARCHAR(200),
    last_checkpoint_timestamp TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- Aggregated shipment state (estado agregado de la guía)
CREATE TABLE shipment_current_state (
    shipment_id UUID PRIMARY KEY REFERENCES shipments(id),
    state_id INTEGER NOT NULL REFERENCES states(id),
    units_in_state INTEGER NOT NULL DEFAULT 0,
    total_units INTEGER NOT NULL,
    progress_percentage DECIMAL(5, 2) DEFAULT 0.00,
    last_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- Users for authentication
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE NOT NULL,
    hashed_password VARCHAR(255) NOT NULL,
    permissions TEXT [] DEFAULT '{}',
    is_active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- Insert default user for testing
INSERT INTO users (email, hashed_password, permissions)
VALUES (
        'admin@tracking.com',
        '$2b$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj0kB0qBpG2O',
        ARRAY ['tracking:read', 'tracking:write', 'admin:all']
    );
-- Password is: admin123
-- Indexes for performance
CREATE INDEX idx_shipments_shipment_id ON shipments(shipment_id);
CREATE INDEX idx_shipment_units_shipment_id ON shipment_units(shipment_id);
CREATE INDEX idx_shipment_units_barcode ON shipment_units(barcode);
CREATE INDEX idx_unit_checkpoint_log_unit_id ON unit_checkpoint_log(unit_id);
CREATE INDEX idx_unit_checkpoint_log_timestamp ON unit_checkpoint_log(checkpoint_timestamp);
CREATE INDEX idx_unit_current_state_state_id ON unit_current_state(state_id);
CREATE INDEX idx_shipment_current_state_state_id ON shipment_current_state(state_id);
CREATE INDEX idx_users_email ON users(email);
-- Triggers for updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column() RETURNS TRIGGER AS $$ BEGIN NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ language 'plpgsql';
CREATE TRIGGER update_shipments_updated_at BEFORE
UPDATE ON shipments FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_unit_current_state_updated_at BEFORE
UPDATE ON unit_current_state FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();