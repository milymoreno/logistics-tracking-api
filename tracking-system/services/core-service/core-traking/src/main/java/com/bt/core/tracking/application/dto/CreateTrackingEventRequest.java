package com.bt.core.tracking.application.dto;

import com.bt.core.tracking.domain.model.TrackingStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 * DTO para la creación de eventos de tracking
 */
public class CreateTrackingEventRequest {
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotNull(message = "Status is required")
    private TrackingStatus status;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    private Map<String, Object> metadata;

    // Constructors
    public CreateTrackingEventRequest() {}

    public CreateTrackingEventRequest(String userId, TrackingStatus status, String description, Map<String, Object> metadata) {
        this.userId = userId;
        this.status = status;
        this.description = description;
        this.metadata = metadata;
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

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}