package cl.venegas.buses_api.infrastructure.persistence.jpa.repo;

import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import cl.venegas.buses_api.infrastructure.persistence.jpa.entity.SeatHoldJpa;

public interface SeatHoldJpaRepository extends JpaRepository<SeatHoldJpa, Long> {
    void deleteByExpiresAtBefore(LocalDateTime now);
}