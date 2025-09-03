package com.bt.core.tracking.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad del dominio que representa un evento de tracking
 */
public class TrackingEvent {
    
    private Long id;
    private String trackingId;
    private String userId;
    private TrackingStatus status;
    private String description;
    private LocalDateTime timestamp;
    private String metadata;
    
    public TrackingEvent() {}
    
    public TrackingEvent(String trackingId, String userId, TrackingStatus status, String description) {
        this.trackingId = trackingId;
        this.userId = userId;
        this.status = status;
        this.description = description;
        this.timestamp = LocalDateTime.now();
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackingEvent that = (TrackingEvent) o;
        return Objects.equals(id, that.id) && 
               Objects.equals(trackingId, that.trackingId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, trackingId);
    }
    
    @Override
    public String toString() {
        return "TrackingEvent{" +
                "id=" + id +
                ", trackingId='" + trackingId + '\'' +
                ", userId='" + userId + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}