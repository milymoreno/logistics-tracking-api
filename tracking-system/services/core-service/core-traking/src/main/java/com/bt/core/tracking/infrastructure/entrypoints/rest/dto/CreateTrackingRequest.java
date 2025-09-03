package com.bt.core.tracking.infrastructure.entrypoints.rest.dto;

import com.bt.core.tracking.domain.model.TrackingStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para la creación de eventos de tracking
 */
public class CreateTrackingRequest {
    
    @NotBlank(message = "User ID es requerido")
    private String userId;
    
    @NotNull(message = "Status es requerido")
    private TrackingStatus status;
    
    @NotBlank(message = "Descripción es requerida")
    private String description;
    
    private String metadata;
    
    public CreateTrackingRequest() {}
    
    public CreateTrackingRequest(String userId, TrackingStatus status, String description) {
        this.userId = userId;
        this.status = status;
        this.description = description;
    }
    
    // Getters and Setters
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public TrackingStatus getStatus() {
        return status;
    }
    
    public void setStatus(TrackingStatus status) {
        this.status = status;
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