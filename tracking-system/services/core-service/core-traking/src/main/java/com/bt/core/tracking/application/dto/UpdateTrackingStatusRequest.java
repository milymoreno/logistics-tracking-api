package com.bt.core.tracking.application.dto;

import com.bt.core.tracking.domain.model.TrackingStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para la actualización del status de tracking
 */
public class UpdateTrackingStatusRequest {
    
    @NotNull(message = "New status is required")
    private TrackingStatus newStatus;
    
    @NotBlank(message = "Description is required")
    private String description;

    // Constructors
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
}