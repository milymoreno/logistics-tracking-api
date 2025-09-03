package com.bt.core.tracking.infrastructure.adapters.persistence;

import com.bt.core.tracking.domain.model.TrackingEvent;
import com.bt.core.tracking.domain.model.TrackingStatus;
import com.bt.core.tracking.domain.ports.TrackingRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

/**
 * Implementación del repositorio de tracking usando JDBC puro
 * Todas las queries están documentadas y optimizadas para PostgreSQL
 */
@ApplicationScoped
public class TrackingRepositoryImpl implements TrackingRepository {
    
    @Inject
    DataSource dataSource;
    
    // =====================================================
    // QUERIES SQL DOCUMENTADAS
    // =====================================================
    
    /**
     * Query para insertar un nuevo evento de tracking
     * Utiliza RETURNING para obtener el ID generado y todos los campos
     */
    private static final String INSERT_TRACKING_EVENT = """
        INSERT INTO tracking_events (tracking_id, user_id, status, description, timestamp, metadata)
        VALUES (?, ?, ?::tracking_status, ?, ?, ?::jsonb)
        RETURNING id, tracking_id, user_id, status, description, timestamp, metadata, created_at, updated_at
        """;
    
    /**
     * Query para buscar evento por ID
     * Utiliza índice primario para búsqueda O(1)
     */
    private static final String FIND_BY_ID = """
        SELECT id, tracking_id, user_id, status, description, timestamp, metadata, created_at, updated_at
        FROM tracking_events
        WHERE id = ?
        """;
    
    /**
     * Query para buscar eventos por tracking_id
     * Utiliza índice idx_tracking_events_tracking_id para optimización
     * Ordenado por timestamp DESC para mostrar eventos más recientes primero
     */
    private static final String FIND_BY_TRACKING_ID = """
        SELECT id, tracking_id, user_id, status, description, timestamp, metadata, created_at, updated_at
        FROM tracking_events
        WHERE tracking_id = ?
        ORDER BY timestamp DESC
        """;
    
    /**
     * Query para buscar eventos por user_id
     * Utiliza índice idx_tracking_events_user_id
     * Incluye paginación implícita con LIMIT para evitar resultados masivos
     */
    private static final String FIND_BY_USER_ID = """
        SELECT id, tracking_id, user_id, status, description, timestamp, metadata, created_at, updated_at
        FROM tracking_events
        WHERE user_id = ?
        ORDER BY created_at DESC
        LIMIT 1000
        """;
    
    /**
     * Query para buscar eventos por status
     * Utiliza índice idx_tracking_events_status
     * Optimizada para reportes y monitoreo
     */
    private static final String FIND_BY_STATUS = """
        SELECT id, tracking_id, user_id, status, description, timestamp, metadata, created_at, updated_at
        FROM tracking_events
        WHERE status = ?::tracking_status
        ORDER BY timestamp DESC
        LIMIT 1000
        """;
    
    /**
     * Query para obtener todos los eventos
     * Con LIMIT para evitar problemas de memoria
     * Ordenado por created_at DESC para mostrar más recientes primero
     */
    private static final String FIND_ALL = """
        SELECT id, tracking_id, user_id, status, description, timestamp, metadata, created_at, updated_at
        FROM tracking_events
        ORDER BY created_at DESC
        LIMIT 1000
        """;
    
    /**
     * Query para obtener el user_id de un tracking_id específico
     * Utilizada para mantener consistencia en actualizaciones de status
     */
    private static final String GET_USER_ID_BY_TRACKING_ID = """
        SELECT user_id
        FROM tracking_events
        WHERE tracking_id = ?
        LIMIT 1
        """;
    
    /**
     * Query para eliminar evento por ID
     * Eliminación física (no soft delete)
     */
    private static final String DELETE_BY_ID = """
        DELETE FROM tracking_events
        WHERE id = ?
        """;
    
    // =====================================================
    // IMPLEMENTACIÓN DE MÉTODOS
    // =====================================================
    
    @Override
    public TrackingEvent save(TrackingEvent trackingEvent) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_TRACKING_EVENT)) {
            
            stmt.setString(1, trackingEvent.getTrackingId());
            stmt.setString(2, trackingEvent.getUserId());
            stmt.setString(3, trackingEvent.getStatus().name());
            stmt.setString(4, trackingEvent.getDescription());
            stmt.setTimestamp(5, Timestamp.valueOf(trackingEvent.getTimestamp()));
            stmt.setString(6, trackingEvent.getMetadata());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTrackingEvent(rs);
                }
            }
            
            throw new RuntimeException("Error al insertar evento de tracking");
            
        } catch (SQLException e) {
            throw new RuntimeException("Error de base de datos al guardar tracking event", e);
        }
    }
    
    @Override
    public Optional<TrackingEvent> findById(Long id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTrackingEvent(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new RuntimeException("Error de base de datos al buscar por ID", e);
        }
    }
    
    @Override
    public List<TrackingEvent> findByTrackingId(String trackingId) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_TRACKING_ID)) {
            
            stmt.setString(1, trackingId);
            
            return executeQueryAndMapResults(stmt);
            
        } catch (SQLException e) {
            throw new RuntimeException("Error de base de datos al buscar por tracking ID", e);
        }
    }
    
    @Override
    public List<TrackingEvent> findByUserId(String userId) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_USER_ID)) {
            
            stmt.setString(1, userId);
            
            return executeQueryAndMapResults(stmt);
            
        } catch (SQLException e) {
            throw new RuntimeException("Error de base de datos al buscar por user ID", e);
        }
    }
    
    @Override
    public List<TrackingEvent> findByStatus(TrackingStatus status) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_STATUS)) {
            
            stmt.setString(1, status.name());
            
            return executeQueryAndMapResults(stmt);
            
        } catch (SQLException e) {
            throw new RuntimeException("Error de base de datos al buscar por status", e);
        }
    }
    
    @Override
    public List<TrackingEvent> findAll() {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_ALL)) {
            
            return executeQueryAndMapResults(stmt);
            
        } catch (SQLException e) {
            throw new RuntimeException("Error de base de datos al obtener todos los eventos", e);
        }
    }
    
    @Override
    public Optional<TrackingEvent> updateStatus(String trackingId, TrackingStatus newStatus, String description) {
        // Primero obtenemos el user_id del tracking existente
        String userId = getUserIdFromTrackingId(trackingId);
        if (userId == null) {
            return Optional.empty();
        }
        
        // Creamos un nuevo evento para mantener el historial completo
        TrackingEvent newEvent = new TrackingEvent(trackingId, userId, newStatus, description);
        return Optional.of(save(newEvent));
    }
    
    @Override
    public boolean deleteById(Long id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_BY_ID)) {
            
            stmt.setLong(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error de base de datos al eliminar evento", e);
        }
    }
    
    // =====================================================
    // MÉTODOS AUXILIARES
    // =====================================================
    
    /**
     * Obtiene el user_id asociado a un tracking_id
     * Utilizado para mantener consistencia en las actualizaciones
     */
    private String getUserIdFromTrackingId(String trackingId) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_USER_ID_BY_TRACKING_ID)) {
            
            stmt.setString(1, trackingId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("user_id");
                }
            }
            
            return null;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener user_id por tracking_id", e);
        }
    }
    
    /**
     * Ejecuta una query y mapea los resultados a una lista de TrackingEvent
     */
    private List<TrackingEvent> executeQueryAndMapResults(PreparedStatement stmt) throws SQLException {
        List<TrackingEvent> events = new ArrayList<>();
        
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                events.add(mapResultSetToTrackingEvent(rs));
            }
        }
        
        return events;
    }
    
    /**
     * Mapea un ResultSet a un objeto TrackingEvent
     * Maneja la conversión de tipos de PostgreSQL a Java
     */
    private TrackingEvent mapResultSetToTrackingEvent(ResultSet rs) throws SQLException {
        TrackingEvent event = new TrackingEvent();
        
        event.setId(rs.getLong("id"));
        event.setTrackingId(rs.getString("tracking_id"));
        event.setUserId(rs.getString("user_id"));
        event.setStatus(TrackingStatus.valueOf(rs.getString("status")));
        event.setDescription(rs.getString("description"));
        
        // Manejo seguro de timestamps
        Timestamp timestamp = rs.getTimestamp("timestamp");
        if (timestamp != null) {
            event.setTimestamp(timestamp.toLocalDateTime());
        }
        
        event.setMetadata(rs.getString("metadata"));
        
        return event;
    }
}