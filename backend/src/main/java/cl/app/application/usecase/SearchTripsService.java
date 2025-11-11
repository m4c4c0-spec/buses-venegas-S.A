package backend.src.main.java.cl.app.application.usecase;

import backend.src.main.java.cl.app.domain.model.Trip;
import backend.src.main.java.cl.app.domain.port.TripRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SearchTripsService {
  private final TripRepository trips;
  public SearchTripsService(TripRepository trips){ this.trips = trips; }
  public List<Trip> handle(String origin, String dest, LocalDate date){
    return trips.findBy(origin, dest, date);
  }
}