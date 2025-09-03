package com.bt.core.tracking.infrastructure.adapters.persistence;

import com.bt.core.tracking.domain.model.TrackingStatus;
import java.time.LocalDateTime;

/**
 * Entidad POJO para tracking events (sin JPA)
 */
public class TrackingEntity {
    
    private Long id;
    private String trackingId;
    private String userId;
    private TrackingStatus status;
    private String description;
    private LocalDateTime timestamp;
    private String metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public TrackingEntity() {}
    
    public TrackingEntity(String trackingId, String userId, TrackingStatus status, String description) {
        this.trackingId = trackingId;
        this.userId = userId;
        this.status = status;
        this.description = description;
        this.timestamp = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}