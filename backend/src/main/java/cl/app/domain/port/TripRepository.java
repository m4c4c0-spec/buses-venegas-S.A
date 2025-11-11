package backend.src.main.java.cl.app.domain.port;

import backend.src.main.java.cl.app.domain.model.Trip;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TripRepository {
  List<Trip> findBy(String origin, String dest, LocalDate date);
  
  Optional<Trip> findById(Long id);
}