package cl.venegas.buses_api.infrastructure.persistence.jpa.mapper;

import cl.venegas.buses_api.domain.model.Trip;
import cl.venegas.buses_api.domain.model.valueobject.Money;
import cl.venegas.buses_api.infrastructure.persistence.jpa.entity.TripJpa;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TripMapper {

    public Trip toDomain(TripJpa entity) {
        if (entity == null) {
            return null;
        }
        return new Trip(
                entity.getId(),
                entity.getOrigin(),
                entity.getDestination(),
                entity.getDepartureTs(),
                entity.getArrivalTs(),
                new Money(BigDecimal.valueOf(entity.getBasePriceClp())));
    }

    public TripJpa toEntity(Trip domain) {
        if (domain == null) {
            return null;
        }
        return new TripJpa(
                domain.id(),
                domain.origin(),
                domain.destination(),
                domain.departureTs(),
                domain.arrivalTs(),
                domain.basePrice().getAmount().intValue());
    }
}
