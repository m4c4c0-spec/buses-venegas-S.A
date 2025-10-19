package cl.venegas.buses_api.infrastructure.persistence.jpa.repo;

import cl.venegas.buses_api.infrastructure.persistence.jpa.entity.SeatHoldJpa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatHoldJpaRepository extends JpaRepository<SeatHoldJpa, Long> {   
    void deleteByExpiresAtBefore(LocalDateTime now);
}