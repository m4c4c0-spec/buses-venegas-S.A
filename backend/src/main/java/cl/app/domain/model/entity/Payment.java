package cl.app.domain.model.entity;

import cl.app.domain.model.entity.Payment;
import cl.app.domain.model.valueobject.PaymentMethod;
import cl.app.domain.model.valueobject.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Payment(Long id,Long bookingId, BigDecimal amount, PaymentMethod method, PaymentStatus status,
    String transactionId,
    String gatewayResponse,
    LocalDateTime createdAt
) {

}