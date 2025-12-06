package cl.venegas.buses_api.interfaces.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.venegas.buses_api.application.usecase.seat.HoldSeatsService;
import cl.venegas.buses_api.domain.model.entity.SeatHold;
import cl.venegas.buses_api.interfaces.web.dto.request.HoldRequest;
import cl.venegas.buses_api.interfaces.web.mapper.SeatHoldDTOMapper;
import jakarta.validation.Valid;

/**
 * Controller: Manejo de reservas temporales de asientos
 * Handles HTTP requests related to seat holds
 */
@RestController
@RequestMapping("/api/v1/holds")
public class SeatHoldController {

    private static final Logger log = LoggerFactory.getLogger(SeatHoldController.class);

    private final HoldSeatsService holdSeatsUseCase;
    private final SeatHoldDTOMapper mapper;

    public SeatHoldController(
            HoldSeatsService holdSeatsUseCase,
            SeatHoldDTOMapper mapper) {
        this.holdSeatsUseCase = holdSeatsUseCase;
        this.mapper = mapper;
    }

    /**
     * POST /api/v1/holds
     * Retiene asientos temporalmente para un usuario
     */
    @PostMapping
    public ResponseEntity<java.util.List<SeatHold>> holdSeats(
            @RequestBody @Valid HoldRequest request) {

        log.info("Recibido POST /api/v1/holds: tripId={}, userId={}, seats={}",
                request.tripId(), request.userId(), request.seatNumbers());

        try {
            var command = mapper.toCommand(request);
            java.util.List<SeatHold> holds = holdSeatsUseCase.execute(command);

            log.info("Asientos retenidos exitosamente: tripId={}, userId={}, cantidad={}",
                    request.tripId(), request.userId(), holds.size());

            return ResponseEntity.status(HttpStatus.CREATED).body(holds);
        } catch (IllegalStateException e) {
            log.warn("Error al retener asientos: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al retener asientos: tripId={}, userId={}",
                    request.tripId(), request.userId(), e);
            throw e;
        }
    }
}
