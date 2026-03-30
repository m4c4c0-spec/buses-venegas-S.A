package cl.venegas.buses_api.interfaces.web.controller;

import cl.venegas.buses_api.application.usecase.trip.SearchTripsUseCase;
import cl.venegas.buses_api.domain.model.entity.Trip;
import cl.venegas.buses_api.interfaces.web.dto.response.TripResponse;
import cl.venegas.buses_api.interfaces.web.mapper.TripDTOMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/trips")

public class TripsController {

    private static final Logger log = LoggerFactory.getLogger(TripsController.class);

    private final SearchTripsUseCase searchTripsUseCase;
    private final TripDTOMapper mapper;

    public TripsController(
            SearchTripsUseCase searchTripsUseCase,
            TripDTOMapper mapper) {
        this.searchTripsUseCase = searchTripsUseCase;
        this.mapper = mapper;
    }

    /**
     * GET /api/v1/trips/search
     * Busca viajes disponibles según origen, destino y fecha
     */
    @GetMapping("/search")
    public ResponseEntity<List<TripResponse>> searchTrips(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        log.info("Recibido GET /api/v1/trips/search: origin={}, destination={}, date={}",
                origin, destination, date);

        try {
            List<Trip> trips = searchTripsUseCase.execute(origin, destination, date);
            List<TripResponse> response = trips.stream()
                    .map(mapper::toResponse)
                    .collect(Collectors.toList());

            if (response.isEmpty()) {
                log.warn("No se encontraron viajes: origin={}, destination={}, date={}",
                        origin, destination, date);
            } else {
                log.info("Búsqueda completada: encontrados {} viajes", response.size());
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error en búsqueda de viajes: origin={}, destination={}, date={}",
                    origin, destination, date, e);
            throw e;
        }
    }

    /**
     * GET /api/v1/trips/{tripId}
     * Obtiene un viaje por su ID
     */
    @GetMapping("/{tripId}")
    public ResponseEntity<TripResponse> getTripById(
            @PathVariable String tripId) {

        log.warn("Endpoint GET /api/v1/trips/{} no implementado (501)", tripId);
        return ResponseEntity.status(501).build();
    }
}
