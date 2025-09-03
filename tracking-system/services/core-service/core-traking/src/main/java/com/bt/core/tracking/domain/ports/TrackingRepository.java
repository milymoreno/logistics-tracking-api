package com.bt.core.tracking.domain.ports;

import com.bt.core.tracking.domain.model.TrackingEvent;
import com.bt.core.tracking.domain.model.TrackingStatus;

import java.util.List;
import java.util.Optional;

/**
 * Puerto para el repositorio de tracking
 */
public interface TrackingRepository {
    
    /**
     * Guarda un evento de tracking
     */
    TrackingEvent save(TrackingEvent trackingEvent);
    
    /**
     * Busca un evento por ID
     */
    Optional<TrackingEvent> findById(Long id);
    
    /**
     * Busca eventos por tracking ID
     */
    List<TrackingEvent> findByTrackingId(String trackingId);
    
    /**
     * Busca eventos por user ID
     */
    List<TrackingEvent> findByUserId(String userId);
    
    /**
     * Busca eventos por status
     */
    List<TrackingEvent> findByStatus(TrackingStatus status);
    
    /**
     * Busca todos los eventos
     */
    List<TrackingEvent> findAll();
    
    /**
     * Actualiza el status de un evento
     */
    Optional<TrackingEvent> updateStatus(String trackingId, TrackingStatus newStatus, String description);
    
    /**
     * Elimina un evento por ID
     */
    boolean deleteById(Long id);
}