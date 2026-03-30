package cl.venegas.buses_api.infrastructure.persistence.jpa.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.venegas.buses_api.infrastructure.persistence.jpa.entity.PaymentJpa;

public interface PaymentJpaRepository extends JpaRepository<PaymentJpa, Long> {
    Optional<PaymentJpa> findByBookingId(Long bookingId);

    Optional<PaymentJpa> findByTransactionId(String transactionId);
}
