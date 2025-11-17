package cl.venegas.buses_api.domain.model.entity;

import java.time.LocalDateTime;

public record Trip(
        Long id,
        String origin,
        String destination,
        LocalDateTime departureTs,
        LocalDateTime arrivalTs,
        Integer basePriceClp
) {
}