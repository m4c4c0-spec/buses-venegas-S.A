package backend.src.main.java.cl.app.domain.port;

import java.util.Optional;
import backend.src.main.java.cl.app.domain.model.Payment;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(Long id);
    Optional<Payment> findByBookingId(Long bookingId);
    Optional<Payment> findByTransactionId(String transactionId);
}