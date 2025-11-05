package cl.venegas.buses_api.domain.port;

import cl.venegas.buses_api.domain.model.Trip;
import java.time.LocalDate;
import java.util.List;

public interface TripRepository {
  List<Trip> findBy(String origin, String dest, LocalDate date);
}