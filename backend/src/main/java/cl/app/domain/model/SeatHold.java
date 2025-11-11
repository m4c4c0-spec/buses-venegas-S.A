package backend.src.main.java.cl.app.domain.model;

import java.time.LocalDateTime;

public record SeatHold(Long id, Long tripId, String seat, Long userId,
                       LocalDateTime expiresAt) {
                        
                       }