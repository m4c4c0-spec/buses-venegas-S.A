package main.java.cl.venegas.buses_api.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


public record Booking(Long id, Long userId, Long TripId, List<String> seats, List<Passenger> passengers, BookingStatus status,
    BigDecimal totalAmount,
    String paymentReference,
    LocalDateTime createdAt,
    LocalDateTime expiresAt) {
    }