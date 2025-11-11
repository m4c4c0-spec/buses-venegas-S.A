package backend.src.main.java.cl.app.infrastructure.persistence.jpa.adapter;

import backend.src.main.java.cl.app.domain.model.Trip;
import backend.src.main.java.cl.app.domain.port.TripRepository;
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