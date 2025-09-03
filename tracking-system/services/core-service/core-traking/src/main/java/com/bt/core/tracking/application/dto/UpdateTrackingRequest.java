package com.bt.core.tracking.application.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateTrackingRequest {
    
    @NotBlank(message = "New status is required")
    private String newStatus;
    
    private String description;
    private String metadata;

    public UpdateTrackingRequest() {
    }

    public UpdateTrackingRequest(String newStatus, String description, String metadata) {
        this.newStatus = newStatus;
        this.description = description;
        this.metadata = metadata;
    }

    // Getters and Setters
    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
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