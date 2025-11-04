package cl.venegas.buses_api.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Payment(Long id,Long bookingId, BigDecimal amount, PaymentMethod method, PaymentStatus status,
    String transactionId,
    String gatewayResponse,
    LocalDateTime createdAt
) {

}