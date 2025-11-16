package cl.app.domain.model.entity;

import cl.app.domain.model.entity.Trip;


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