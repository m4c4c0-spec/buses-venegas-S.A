package cl.venegas.buses_api.domain.repository;

import cl.venegas.buses_api.domain.model.entity.Trip;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TripRepository {
  List<Trip> findBy(String origin, String dest, LocalDate date);

  Optional<Trip> findById(Long id);
}