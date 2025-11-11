package backend.src.main.java.cl.app.infrastructure.persistence.jpa.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.src.main.java.cl.app.domain.model.BookingStatus;
import backend.src.main.java.cl.app.infrastructure.persistence.jpa.entity.BookingJpa;

public interface BookingJpaRepository extends JpaRepository<BookingJpa, Long> {
    List<BookingJpa> findByUserId(Long userId);
    List<BookingJpa> findByStatus(BookingStatus status);
    List<BookingJpa> findByTripId(Long tripId);

}