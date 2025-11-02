package cl.venegas.buses_api.infrastructure.persistence.jpa.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cl.venegas.buses_api.domain.model.Payment;
import cl.venegas.buses_api.domain.model.PaymentMethod;
import cl.venegas.buses_api.domain.model.PaymentStatus;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(name = "transaction_id", unique = true)
    private String transactionId;

    @Column(name = "gateway_response", columnDefinition = "TEXT")
    private String gatewayResponse;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Payment toDomain() {
        return new Payment(
                id,
                bookingId,
                amount,
                method,
                status,
                transactionId,
                gatewayResponse,
                createdAt
        );
    }

    public static PaymentJpa fromDomain(Payment payment) {
        return new PaymentJpa(
                payment.id(),
                payment.bookingId(),
                payment.amount(),
                payment.method(),
                payment.status(),
                payment.transactionId(),
                payment.gatewayResponse(),
                payment.createdAt()
        );
    }
}