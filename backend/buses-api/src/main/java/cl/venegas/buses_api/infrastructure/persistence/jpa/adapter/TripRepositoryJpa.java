package cl.venegas.buses_api.infrastructure.persistence.jpa.adapter;

import cl.venegas.buses_api.domain.model.Trip;
import cl.venegas.buses_api.domain.port.TripRepository;
import cl.venegas.buses_api.infrastructure.persistence.jpa.mapper.TripMapper;
import cl.venegas.buses_api.infrastructure.persistence.jpa.repo.TripJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class TripRepositoryJpa implements TripRepository {

    private final TripJpaRepository repo;
    private final TripMapper mapper;

    public TripRepositoryJpa(TripJpaRepository repo, TripMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public List<Trip> findBy(String origin, String dest, LocalDate date) {
        // Implementación real pendiente o stub
        return List.of();
    }

    @Override
    public Optional<Trip> findById(Long id) {
        return repo.findById(id)
                .map(mapper::toDomain);
    }
}