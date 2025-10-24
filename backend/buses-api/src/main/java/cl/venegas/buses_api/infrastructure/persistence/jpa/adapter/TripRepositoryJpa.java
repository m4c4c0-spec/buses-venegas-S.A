package cl.venegas.buses_api.infrastructure.persistence.jpa.adapter;

import cl.venegas.buses_api.domain.model.Trip;
import cl.venegas.buses_api.domain.port.TripRepository;
import cl.venegas.buses_api.infrastructure.persistence.jpa.entity.TripJpa;
import cl.venegas.buses_api.infrastructure.persistence.jpa.repo.TripJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class TripRepositoryJpa implements TripRepository {
  private final TripJpaRepository repo;
  public TripRepositoryJpa(TripJpaRepository repo){ this.repo = repo; }

  @Override
  public List<Trip> findBy(String origin, String dest, LocalDate date) {
    var from = date.atStartOfDay();
    var to = date.plusDays(1).atStartOfDay();
    return repo.findByOriginAndDestinationAndDepartureTsBetween(origin, dest, from, to)
               .stream().map(TripJpa::toDomain).toList();
  }
}