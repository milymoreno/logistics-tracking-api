package com.bt.core.tracking.application.handler;

import com.bt.core.tracking.domain.model.TrackingEvent;
import com.bt.core.tracking.domain.model.TrackingStatus;
import com.bt.core.tracking.domain.service.TrackingService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * Handler de la capa de aplicación para casos de uso de tracking
 */
@ApplicationScoped
public class TrackingHandler {
    
    @Inject
    TrackingService trackingService;
    
    /**
     * Maneja la creación de un nuevo evento de tracking
     */
    public TrackingEvent handleCreateTracking(String userId, TrackingStatus status, String description) {
        validateUserId(userId);
        validateDescription(description);
        
        return trackingService.createTrackingEvent(userId, status, description);
    }

    /**
     * Crea un nuevo evento de tracking con metadata
     */
    public TrackingEvent createTrackingEvent(String userId, TrackingStatus status, String description, java.util.Map<String, Object> metadata) {
        validateUserId(userId);
        validateDescription(description);
        
        return trackingService.createTrackingEvent(userId, status, description);
    }

    /**
     * Obtiene un tracking por su ID de tracking (string)
     */
    public TrackingEvent getTrackingById(String trackingId) {
        validateTrackingId(trackingId);
        
        List<TrackingEvent> history = trackingService.getTrackingHistory(trackingId);
        return history.isEmpty() ? null : history.get(0);
    }

    /**
     * Actualiza el status de un tracking
     */
    public TrackingEvent updateTrackingStatus(String trackingId, TrackingStatus newStatus, String description) {
        validateTrackingId(trackingId);
        validateDescription(description);
        
        Optional<TrackingEvent> updated = trackingService.updateTrackingStatus(trackingId, newStatus, description);
        return updated.orElse(null);
    }

    /**
     * Obtiene trackings por usuario
     */
    public List<TrackingEvent> getTrackingsByUserId(String userId) {
        validateUserId(userId);
        
        return trackingService.getUserTrackings(userId);
    }

    /**
     * Obtiene trackings por status
     */
    public List<TrackingEvent> getTrackingsByStatus(TrackingStatus status) {
        return trackingService.getTrackingsByStatus(status);
    }

    /**
     * Elimina un tracking
     */
    public boolean deleteTracking(String trackingId) {
        validateTrackingId(trackingId);
        
        // Por ahora retornamos false ya que no implementamos eliminación
        return false;
    }
    
    /**
     * Maneja la actualización de status de tracking
     */
    public Optional<TrackingEvent> handleUpdateTrackingStatus(String trackingId, TrackingStatus newStatus, String description) {
        validateTrackingId(trackingId);
        validateDescription(description);
        
        return trackingService.updateTrackingStatus(trackingId, newStatus, description);
    }
    
    /**
     * Maneja la consulta de historial de tracking
     */
    public List<TrackingEvent> handleGetTrackingHistory(String trackingId) {
        validateTrackingId(trackingId);
        
        return trackingService.getTrackingHistory(trackingId);
    }
    
    /**
     * Maneja la consulta de trackings por usuario
     */
    public List<TrackingEvent> handleGetUserTrackings(String userId) {
        validateUserId(userId);
        
        return trackingService.getUserTrackings(userId);
    }
    
    /**
     * Maneja la consulta de trackings por status
     */
    public List<TrackingEvent> handleGetTrackingsByStatus(TrackingStatus status) {
        return trackingService.getTrackingsByStatus(status);
    }
    
    /**
     * Maneja la consulta de todos los trackings
     */
    public List<TrackingEvent> handleGetAllTrackings() {
        return trackingService.getAllTrackings();
    }
    
    /**
     * Maneja la consulta de tracking por ID
     */
    public Optional<TrackingEvent> handleGetTrackingById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID debe ser un número positivo");
        }
        
        return trackingService.getTrackingById(id);
    }
    
    // Métodos de validación
    private void validateUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID no puede estar vacío");
        }
    }
    
    private void validateTrackingId(String trackingId) {
        if (trackingId == null || trackingId.trim().isEmpty()) {
            throw new IllegalArgumentException("Tracking ID no puede estar vacío");
        }
    }
    
    private void validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Descripción no puede estar vacía");
        }
    }
}