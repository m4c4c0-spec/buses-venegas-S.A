package cl.venegas.buses_api.infrastructure.persistence.jpa.adapter;

import cl.venegas.buses_api.domain.model.entity.Trip;
import cl.venegas.buses_api.domain.repository.TripRepository;
import cl.venegas.buses_api.infrastructure.persistence.jpa.entity.TripJpa;
import cl.venegas.buses_api.infrastructure.persistence.jpa.repo.TripJpaRepository;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@SuppressWarnings("null")
public class TripRepositoryJpa implements TripRepository {

    private final TripJpaRepository repository;

    public TripRepositoryJpa(TripJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Trip> findBy(String origin, String dest, LocalDate date) {
        return repository.findByOriginAndDestinationAndDepartureTsBetween(
                origin, dest, date.atStartOfDay(), date.atTime(java.time.LocalTime.MAX))
                .stream()
                .map(TripJpa::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Trip> findById(Long id) {
        return repository.findById(id).map(TripJpa::toDomain);
    }
}