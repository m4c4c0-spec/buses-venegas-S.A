package cl.venegas.buses_api.infrastructure.persistence.jpa.repo;

import cl.venegas.buses_api.domain.model.SeatHold;
import cl.venegas.buses_api.infrastructure.persistence.jpa.entity.SeatHoldJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


public interface SeatHoldJpaRepository extends JpaRepository<SeatHoldJpa, Long> {   
    void deleteByExpiresAtBefore(LocalDateTime now);

    Collection<SeatHold> findByTripIdAndSeatIn(Long tripId, List<String> seatNumbers);
}