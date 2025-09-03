package com.bt.core.tracking.domain.service;

import com.bt.core.tracking.domain.model.TrackingEvent;
import com.bt.core.tracking.domain.model.TrackingStatus;
import com.bt.core.tracking.domain.ports.TrackingRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio del dominio para la lógica de negocio de tracking
 */
@ApplicationScoped
public class TrackingService {
    
    @Inject
    TrackingRepository trackingRepository;
    
    /**
     * Crea un nuevo evento de tracking
     */
    public TrackingEvent createTrackingEvent(String userId, TrackingStatus status, String description) {
        String trackingId = generateTrackingId();
        TrackingEvent event = new TrackingEvent(trackingId, userId, status, description);
        return trackingRepository.save(event);
    }
    
    /**
     * Actualiza el status de un tracking
     */
    public Optional<TrackingEvent> updateTrackingStatus(String trackingId, TrackingStatus newStatus, String description) {
        return trackingRepository.updateStatus(trackingId, newStatus, description);
    }
    
    /**
     * Obtiene el historial de tracking por tracking ID
     */
    public List<TrackingEvent> getTrackingHistory(String trackingId) {
        return trackingRepository.findByTrackingId(trackingId);
    }
    
    /**
     * Obtiene todos los trackings de un usuario
     */
    public List<TrackingEvent> getUserTrackings(String userId) {
        return trackingRepository.findByUserId(userId);
    }
    
    /**
     * Obtiene trackings por status
     */
    public List<TrackingEvent> getTrackingsByStatus(TrackingStatus status) {
        return trackingRepository.findByStatus(status);
    }
    
    /**
     * Obtiene todos los trackings
     */
    public List<TrackingEvent> getAllTrackings() {
        return trackingRepository.findAll();
    }
    
    /**
     * Obtiene un tracking por ID
     */
    public Optional<TrackingEvent> getTrackingById(Long id) {
        return trackingRepository.findById(id);
    }
    
    /**
     * Genera un ID único para tracking
     */
    private String generateTrackingId() {
        return "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}