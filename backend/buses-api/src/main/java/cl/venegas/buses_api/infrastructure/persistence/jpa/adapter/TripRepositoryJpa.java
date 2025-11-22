package cl.venegas.buses_api.infrastructure.persistence.jpa.adapter;

import cl.venegas.buses_api.domain.model.entity.Trip;
import cl.venegas.buses_api.domain.repository.TripRepository;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class TripRepositoryJpa implements TripRepository {

    @Override
    public List<Trip> findBy(String origin, String dest, LocalDate date) {
        return List.of();
    }

    @Override
    public Optional<Trip> findById(Long id) {
        return Optional.empty();
    }
}