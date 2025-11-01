package cl.venegas.buses_api.infrastructure.persistence.jpa.repo;

import cl.venegas.buses_api.infrastructure.persistence.jpa.entity.SeatHoldJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;


public interface SeatHoldJpaRepository extends JpaRepository<SeatHoldJpa, Long> {   
    void deleteByExpiresAtBefore(LocalDateTime now);
}