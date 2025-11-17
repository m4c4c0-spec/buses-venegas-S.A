package cl.venegas.buses_api.domain.model.entity;

import java.math.BigDecimal;
import java.time.Duration;


public record Route(Long id, String origin, String destination, Duration estimatedDuration, BigDecimal basePrice, Boolean active
) {

}