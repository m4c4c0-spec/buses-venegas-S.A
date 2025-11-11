package backend.src.main.java.cl.app.domain.model;

import java.math.BigDecimal;
import java.time.Duration;


public record Route(Long id, String origin, String destination, Duration estimatedDuration, BigDecimal basePrice, Boolean active
) {

}