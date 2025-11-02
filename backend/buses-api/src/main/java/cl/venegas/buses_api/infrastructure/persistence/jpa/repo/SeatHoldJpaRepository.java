package cl.venegas.buses_api.infrastructure.persistence.jpa.repo;

import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< HEAD
import java.time.LocalDateTime;

=======
import cl.venegas.buses_api.infrastructure.persistence.jpa.entity.SeatHoldJpa;
>>>>>>> c6597ae (Merge branch 'feature/backend-infrastructure' into feature/backend-domain)

public interface SeatHoldJpaRepository extends JpaRepository<SeatHoldJpa, Long> {
    void deleteByExpiresAtBefore(LocalDateTime now);
}