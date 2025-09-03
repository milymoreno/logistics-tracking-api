package com.bt.core.tracking.infrastructure.entrypoints.rest.dto;

import com.bt.core.tracking.domain.model.TrackingEvent;
import com.bt.core.tracking.domain.model.TrackingStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para eventos de tracking
 */
public class TrackingResponse {
    
    private Long id;
    private String trackingId;
    private String userId;
    private TrackingStatus status;
    private String description;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    private String metadata;
    
    public TrackingResponse() {}
    
    public TrackingResponse(TrackingEvent trackingEvent) {
        this.id = trackingEvent.getId();
        this.trackingId = trackingEvent.getTrackingId();
        this.userId = trackingEvent.getUserId();
        this.status = trackingEvent.getStatus();
        this.description = trackingEvent.getDescription();
        this.timestamp = trackingEvent.getTimestamp();
        this.metadata = trackingEvent.getMetadata();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTrackingId() {
        return trackingId;
    }
    
    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }
    
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
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getMetadata() {
        return metadata;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}