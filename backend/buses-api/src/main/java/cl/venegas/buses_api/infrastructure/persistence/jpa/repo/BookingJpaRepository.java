package cl.venegas.buses_api.infrastructure.persistence.jpa.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.venegas.buses_api.domain.model.valueobject.BookingStatus;
import cl.venegas.buses_api.infrastructure.persistence.jpa.entity.BookingJpa;

public interface BookingJpaRepository extends JpaRepository<BookingJpa, Long> {
    List<BookingJpa> findByUserId(Long userId);
    List<BookingJpa> findByStatus(BookingStatus status);
    List<BookingJpa> findByTripId(Long tripId);

}