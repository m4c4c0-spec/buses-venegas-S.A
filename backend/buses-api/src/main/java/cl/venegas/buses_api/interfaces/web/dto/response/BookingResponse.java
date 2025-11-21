package cl.venegas.buses_api.interfaces.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import cl.venegas.buses_api.domain.model.valueobject.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(
        Long id,
        Long tripId,
        Long userId,
        List<String> seats,
        BookingStatus status,
        BigDecimal totalAmount,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime expiresAt) {
}
