package cl.venegas.buses_api.domain.model;

import cl.venegas.buses_api.domain.model.valueobject.Money;
import java.time.LocalDateTime;

public record Trip(
                Long id,
                String origin,
                String destination,
                LocalDateTime departureTs,
                LocalDateTime arrivalTs,
                Money basePrice) {
}