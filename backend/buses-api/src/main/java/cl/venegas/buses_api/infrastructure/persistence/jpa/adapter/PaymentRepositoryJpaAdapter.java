package cl.venegas.buses_api.infrastructure.persistence.jpa.adapter;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import cl.venegas.buses_api.domain.model.entity.Payment;
import cl.venegas.buses_api.domain.repository.PaymentRepository;
import cl.venegas.buses_api.infrastructure.persistence.jpa.entity.PaymentJpa;
import cl.venegas.buses_api.infrastructure.persistence.jpa.repo.PaymentJpaRepository;

@Repository
public class PaymentRepositoryJpaAdapter implements PaymentRepository {

    private final PaymentJpaRepository jpaRepository;

    public PaymentRepositoryJpaAdapter(PaymentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Payment save(Payment payment) {
        PaymentJpa jpa = PaymentJpa.fromDomain(payment);
        PaymentJpa saved = jpaRepository.save(jpa);
        return saved.toDomain();
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return jpaRepository.findById(id).map(PaymentJpa::toDomain);
    }

    @Override
    public Optional<Payment> findByBookingId(Long bookingId) {
        return jpaRepository.findByBookingId(bookingId).map(PaymentJpa::toDomain);
    }

    @Override
    public Optional<Payment> findByTransactionId(String transactionId) {
        return jpaRepository.findByTransactionId(transactionId).map(PaymentJpa::toDomain);
    }
}
