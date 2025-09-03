package com.bt.core.tracking.infrastructure.entrypoints.rest;

import com.bt.core.tracking.application.dto.CreateTrackingEventRequest;
import com.bt.core.tracking.application.dto.TrackingResponse;
import com.bt.core.tracking.application.dto.UpdateTrackingStatusRequest;
import com.bt.core.tracking.application.handler.TrackingHandler;
import com.bt.core.tracking.domain.model.TrackingEvent;
import com.bt.core.tracking.domain.model.TrackingStatus;
import com.bt.core.tracking.infrastructure.security.SecurityContext;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller para operaciones de tracking
 * Proporciona endpoints para crear, consultar y actualizar eventos de tracking
 */
@Path("/api/v1/tracking")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Tracking", description = "Operaciones de seguimiento y tracking de eventos")
public class TrackingController {

    @Inject
    TrackingHandler trackingHandler;

    @Inject
    SecurityContext securityContext;

    @POST
    @Operation(
        summary = "Crear nuevo evento de tracking",
        description = "Crea un nuevo evento de tracking con ID único generado automáticamente"
    )
    public Response createTrackingEvent(@Valid CreateTrackingEventRequest request) {
        try {
            TrackingEvent event = trackingHandler.createTrackingEvent(
                request.getUserId(),
                request.getStatus(),
                request.getDescription(),
                request.getMetadata()
            );
            
            TrackingResponse response = TrackingResponse.fromTrackingEvent(event);
            return Response.status(Response.Status.CREATED).entity(response).build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating tracking event: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{trackingId}")
    @Operation(
        summary = "Obtener tracking por ID",
        description = "Recupera un evento de tracking específico por su ID único"
    )
    public Response getTrackingById(
            @Parameter(description = "ID único del tracking", required = true)
            @PathParam("trackingId") String trackingId) {
        
        try {
            TrackingEvent event = trackingHandler.getTrackingById(trackingId);
            if (event == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Tracking not found with ID: " + trackingId)
                        .build();
            }
            
            TrackingResponse response = TrackingResponse.fromTrackingEvent(event);
            return Response.ok(response).build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving tracking: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{trackingId}/status")
    @Operation(
        summary = "Actualizar status de tracking",
        description = "Actualiza el status de un tracking existente"
    )
    public Response updateTrackingStatus(
            @Parameter(description = "ID único del tracking", required = true)
            @PathParam("trackingId") String trackingId,
            @Valid UpdateTrackingStatusRequest request) {
        
        try {
            TrackingEvent updatedEvent = trackingHandler.updateTrackingStatus(
                trackingId,
                request.getNewStatus(),
                request.getDescription()
            );
            
            if (updatedEvent == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Tracking not found with ID: " + trackingId)
                        .build();
            }
            
            TrackingResponse response = TrackingResponse.fromTrackingEvent(updatedEvent);
            return Response.ok(response).build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating tracking status: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/user/{userId}")
    @Operation(
        summary = "Obtener trackings por usuario",
        description = "Recupera todos los eventos de tracking asociados a un usuario específico"
    )
    public Response getTrackingsByUserId(
            @Parameter(description = "ID del usuario", required = true)
            @PathParam("userId") String userId) {
        
        try {
            List<TrackingEvent> events = trackingHandler.getTrackingsByUserId(userId);
            List<TrackingResponse> responses = events.stream()
                    .map(TrackingResponse::fromTrackingEvent)
                    .collect(Collectors.toList());
            
            return Response.ok(responses).build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving trackings for user: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/status/{status}")
    @Operation(
        summary = "Obtener trackings por status",
        description = "Recupera todos los eventos de tracking que tienen un status específico"
    )
    public Response getTrackingsByStatus(
            @Parameter(description = "Status del tracking", required = true)
            @PathParam("status") String status) {
        
        try {
            TrackingStatus trackingStatus = TrackingStatus.valueOf(status.toUpperCase());
            List<TrackingEvent> events = trackingHandler.getTrackingsByStatus(trackingStatus);
            List<TrackingResponse> responses = events.stream()
                    .map(TrackingResponse::fromTrackingEvent)
                    .collect(Collectors.toList());
            
            return Response.ok(responses).build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid status: " + status)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving trackings by status: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{trackingId}")
    @Operation(
        summary = "Eliminar tracking",
        description = "Elimina un evento de tracking específico del sistema"
    )
    public Response deleteTracking(
            @Parameter(description = "ID único del tracking", required = true)
            @PathParam("trackingId") String trackingId) {
        
        try {
            boolean deleted = trackingHandler.deleteTracking(trackingId);
            if (!deleted) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Tracking not found with ID: " + trackingId)
                        .build();
            }
            
            return Response.noContent().build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error deleting tracking: " + e.getMessage())
                    .build();
        }
    }
}