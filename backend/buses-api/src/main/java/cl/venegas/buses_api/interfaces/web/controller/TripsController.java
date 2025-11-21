package cl.venegas.buses_api.interfaces.web.controller;

import cl.venegas.buses_api.application.usecase.trip.SearchTripsUseCase;
import cl.venegas.buses_api.domain.model.entity.Trip;
import cl.venegas.buses_api.interfaces.web.dto.response.TripResponse;
import cl.venegas.buses_api.interfaces.web.mapper.TripDTOMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/trips")
@CrossOrigin(origins = "*")
public class TripsController {

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
     * busca las rutas del viaje
     * 
     * ciudad de origen
     * 
     * @param origin
     *                    ciudad del destino
     * @param destination
     *                    fecha en la que viaja (formato: yyyy-MM-dd)
     * @param date
     */
    @GetMapping("/search")
    public ResponseEntity<List<TripResponse>> searchTrips(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<Trip> trips = searchTripsUseCase.execute(origin, destination, date);

        List<TripResponse> response = trips.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/trips/{tripId}
     * Se obtiene el viaje mediante el id
     */
    @GetMapping("/{tripId}")
    public ResponseEntity<TripResponse> getTripById(
            @PathVariable String tripId) {

        return ResponseEntity.status(501).build();
    }
}