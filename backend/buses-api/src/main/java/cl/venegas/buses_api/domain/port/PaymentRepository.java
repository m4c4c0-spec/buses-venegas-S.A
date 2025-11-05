package cl.venegas.buses_api.domain.port;

import java.util.Optional;
import cl.venegas.buses_api.domain.model.Payment;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(Long id);
    Optional<Payment> findByBookingId(Long bookingId);
    Optional<Payment> findByTransactionId(String transactionId);
}