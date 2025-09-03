package com.bt.core.tracking.infrastructure.entrypoints.rest.dto;

import com.bt.core.tracking.domain.model.TrackingStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para la actualización de status de tracking
 */
public class UpdateTrackingStatusRequest {
    
    @NotNull(message = "Nuevo status es requerido")
    private TrackingStatus newStatus;
    
    @NotBlank(message = "Descripción es requerida")
    private String description;
    
    private String metadata;
    
    public UpdateTrackingStatusRequest() {}
    
    public UpdateTrackingStatusRequest(TrackingStatus newStatus, String description) {
        this.newStatus = newStatus;
        this.description = description;
    }
    
    // Getters and Setters
    public TrackingStatus getNewStatus() {
        return newStatus;
    }
    
    public void setNewStatus(TrackingStatus newStatus) {
        this.newStatus = newStatus;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getMetadata() {
        return metadata;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}