package cl.venegas.buses_api.domain.model.entity;

import cl.venegas.buses_api.domain.model.valueobject.PaymentMethod;
import cl.venegas.buses_api.domain.model.valueobject.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;



public record Payment(Long id,Long bookingId, BigDecimal amount, PaymentMethod method, PaymentStatus status,
    String transactionId,
    String gatewayResponse,
    LocalDateTime createdAt
) {

}