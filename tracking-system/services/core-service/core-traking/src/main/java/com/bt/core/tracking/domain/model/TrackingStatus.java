package com.bt.core.tracking.domain.model;

/**
 * Enum que representa los diferentes estados de tracking
 */
public enum TrackingStatus {
    CREATED("Creado"),
    IN_PROGRESS("En Progreso"),
    PROCESSING("Procesando"),
    COMPLETED("Completado"),
    FAILED("Fallido"),
    CANCELLED("Cancelado"),
    PENDING("Pendiente"),
    APPROVED("Aprobado"),
    REJECTED("Rechazado");
    
    private final String description;
    
    TrackingStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getCode() {
        return this.name();
    }
    
    @Override
    public String toString() {
        return this.description;
    }
}