package cl.venegas.buses_api.application.usecase.trip;

import cl.venegas.buses_api.domain.model.entity.Trip;
import cl.venegas.buses_api.domain.repository.TripRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SearchTripsUseCase {
  private final TripRepository trips;

  public SearchTripsUseCase(TripRepository trips) {
    this.trips = trips;
  }

  public List<Trip> execute(String origin, String dest, LocalDate date) {
    return trips.findBy(origin, dest, date);
  }
}