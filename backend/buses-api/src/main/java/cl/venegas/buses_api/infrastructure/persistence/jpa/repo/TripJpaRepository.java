package cl.venegas.buses_api.infrastructure.persistence.jpa.repo;

import cl.venegas.buses_api.infrastructure.persistence.jpa.entity.TripJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface TripJpaRepository extends JpaRepository<TripJpa, Long> {
  List<TripJpa> findByOriginAndDestinationAndDepartureTsBetween(
          String origin, String dest, LocalDateTime from, LocalDateTime to);
}