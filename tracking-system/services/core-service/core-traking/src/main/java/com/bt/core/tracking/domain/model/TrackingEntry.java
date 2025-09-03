package com.bt.core.tracking.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class TrackingEntry {
    private Long id;
    private String transactionId;
    private String userId;
    private TrackingStatus status;
    private TrackingStatus previousStatus;
    private String description;
    private String metadata;
    private LocalDateTime timestamp;
    private String createdBy;

    public TrackingEntry() {
    }

    public TrackingEntry(String transactionId, String userId, TrackingStatus status, 
                        String description, String createdBy) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.status = status;
        this.description = description;
        this.createdBy = createdBy;
        this.timestamp = LocalDateTime.now();
    }

    public TrackingEntry(String transactionId, String userId, TrackingStatus status, 
                        TrackingStatus previousStatus, String description, String createdBy) {
        this(transactionId, userId, status, description, createdBy);
        this.previousStatus = previousStatus;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
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

    public TrackingStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(TrackingStatus previousStatus) {
        this.previousStatus = previousStatus;
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackingEntry that = (TrackingEntry) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(transactionId, that.transactionId) &&
               Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, transactionId, timestamp);
    }

    @Override
    public String toString() {
        return "TrackingEntry{" +
                "id=" + id +
                ", transactionId='" + transactionId + '\'' +
                ", userId='" + userId + '\'' +
                ", status=" + status +
                ", previousStatus=" + previousStatus +
                ", description='" + description + '\'' +
                ", timestamp=" + timestamp +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }
}