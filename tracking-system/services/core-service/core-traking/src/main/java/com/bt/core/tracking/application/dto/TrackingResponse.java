package com.bt.core.tracking.application.dto;

import com.bt.core.tracking.domain.model.TrackingEntry;
import com.bt.core.tracking.domain.model.TrackingEvent;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class TrackingResponse {
    
    private Long id;
    private String transactionId;
    private String userId;
    private String status;
    private String previousStatus;
    private String description;
    private String metadata;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    private String createdBy;

    public TrackingResponse() {
    }

    public TrackingResponse(TrackingEntry entry) {
        this.id = entry.getId();
        this.transactionId = entry.getTransactionId();
        this.userId = entry.getUserId();
        this.status = entry.getStatus() != null ? entry.getStatus().getCode() : null;
        this.previousStatus = entry.getPreviousStatus() != null ? entry.getPreviousStatus().getCode() : null;
        this.description = entry.getDescription();
        this.metadata = entry.getMetadata();
        this.timestamp = entry.getTimestamp();
        this.createdBy = entry.getCreatedBy();
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(String previousStatus) {
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

    /**
     * Factory method para crear TrackingResponse desde TrackingEvent
     */
    public static TrackingResponse fromTrackingEvent(TrackingEvent event) {
        TrackingResponse response = new TrackingResponse();
        response.setTransactionId(event.getTrackingId());
        response.setUserId(event.getUserId());
        response.setStatus(event.getStatus() != null ? event.getStatus().getCode() : null);
        response.setDescription(event.getDescription());
        response.setTimestamp(event.getTimestamp());
        response.setMetadata(event.getMetadata());
        return response;
    }
}