package backend.src.main.java.cl.app.infrastructure.persistence.jpa.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import backend.src.main.java.cl.app.infrastructure.persistence.jpa.entity.SeatHoldJpa;

public interface SeatHoldJpaRepository extends JpaRepository<SeatHoldJpa, Long> {
    
    List<SeatHoldJpa> findByTripIdAndSeatIn(Long tripId, List<String> seatNumbers);
    
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}