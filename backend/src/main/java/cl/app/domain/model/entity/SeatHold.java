package cl.app.domain.model.entity;

import cl.app.domain.model.entity.SeatHold;


import java.time.LocalDateTime;

public record SeatHold(Long id, Long tripId, String seat, Long userId,
                       LocalDateTime expiresAt) {
                        
                       }