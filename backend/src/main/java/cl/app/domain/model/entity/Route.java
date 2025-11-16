package cl.app.domain.model.entity;

import cl.app.domain.model.entity.Route;

import java.math.BigDecimal;
import java.time.Duration;


public record Route(Long id, String origin, String destination, Duration estimatedDuration, BigDecimal basePrice, Boolean active
) {

}