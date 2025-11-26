package cl.venegas.buses_api.domain.model;

import cl.venegas.buses_api.domain.model.valueobject.SeatNumber;
import java.time.LocalDateTime;

public record SeatHold(Long id, Long tripId, SeatNumber seat, Long userId,
        LocalDateTime expiresAt) {

}