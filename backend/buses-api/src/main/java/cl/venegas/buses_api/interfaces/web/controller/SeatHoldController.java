package cl.venegas.buses_api.interfaces.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.venegas.buses_api.application.usecase.seat.HoldSeatsService;
import cl.venegas.buses_api.domain.model.entity.SeatHold;
import cl.venegas.buses_api.interfaces.web.dto.request.HoldRequest;
import cl.venegas.buses_api.interfaces.web.mapper.SeatHoldDTOMapper;
import jakarta.validation.Valid;

//Controller: Manejo de reservas de asientos
//Handles HTTP requests related to seat holds:
// - Hold seats temporarily
// - Release holds
@RestController
@RequestMapping("/api/v1/holds")
@CrossOrigin(origins = "*")
public class SeatHoldController {

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
     * Mantiene los asientos del usuario temporalmente
     */
    @PostMapping
    public ResponseEntity<java.util.List<SeatHold>> holdSeats(
            @RequestBody @Valid HoldRequest request) {

        var command = mapper.toCommand(request);

        java.util.List<SeatHold> holds = holdSeatsUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(holds);
    }
}
