package backend.src.main.java.cl.app.presentation.http.response;

import backend.src.main.java.cl.app.domain.model.Trip;

public record TripResponse(Long id, String origin, String destination,
                           String departureTs, String arrivalTs,
                           Integer basePriceClp) {
  public static TripResponse from(Trip t){
    return new TripResponse(t.id(), t.origin(), t.destination(),
      t.departureTs().toString(), t.arrivalTs().toString(), t.basePriceClp());
  }
}