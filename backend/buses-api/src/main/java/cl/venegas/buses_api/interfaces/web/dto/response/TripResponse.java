package cl.venegas.buses_api.interfaces.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TripResponse(
        Long id,

        String origin,

        String destination,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime departureTime,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime arrivalTime,

        BigDecimal basePrice,

        Integer availableSeats) {
}