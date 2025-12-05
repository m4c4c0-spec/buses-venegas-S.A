package cl.venegas.buses_api.interfaces.web.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import cl.venegas.buses_api.domain.model.valueobject.PaymentMethod;
import cl.venegas.buses_api.domain.model.valueobject.PaymentStatus;

public record PaymentResponse(
        Long id,
        Long bookingId,
        BigDecimal amount,
        PaymentMethod method,
        PaymentStatus status,
        String transactionId,
        LocalDateTime createdAt) {
}
