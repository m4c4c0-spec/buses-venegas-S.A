package cl.venegas.buses_api.domain.repository;

import cl.venegas.buses_api.domain.model.entity.Payment;

import java.util.Optional;



public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(Long id);
    Optional<Payment> findByBookingId(Long bookingId);
    Optional<Payment> findByTransactionId(String transactionId);
}