package cl.venegas.buses_api.interfaces.web.mapper;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import cl.venegas.buses_api.application.Query.SearchTripsQuery;
import cl.venegas.buses_api.domain.model.entity.Trip;
import cl.venegas.buses_api.interfaces.web.dto.response.TripResponse;

@Component
public class TripDTOMapper {

    public SearchTripsQuery toSearchQuery(String origin, String destination, LocalDate date) {
        return new SearchTripsQuery(origin, destination, date);
    }

    public TripResponse toResponse(Trip trip) {
        return new TripResponse(
                trip.id(),
                trip.origin(),
                trip.destination(),
                trip.departureTs(),
                trip.arrivalTs(),
                java.math.BigDecimal.valueOf(trip.basePriceClp()),
                0);
    }
}