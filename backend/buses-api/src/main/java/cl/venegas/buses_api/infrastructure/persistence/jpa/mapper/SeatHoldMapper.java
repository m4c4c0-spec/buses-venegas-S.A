package cl.venegas.buses_api.infrastructure.persistence.jpa.mapper;

import cl.venegas.buses_api.domain.model.SeatHold;
import cl.venegas.buses_api.domain.model.valueobject.SeatNumber;
import cl.venegas.buses_api.infrastructure.persistence.jpa.entity.SeatHoldJpa;
import org.springframework.stereotype.Component;

@Component
public class SeatHoldMapper {

    public SeatHold toDomain(SeatHoldJpa entity) {
        return new SeatHold(
                entity.getId(),
                entity.getTripId(),
                new SeatNumber(entity.getSeat()),
                entity.getUserId(),
                entity.getExpiresAt());
    }

    public SeatHoldJpa toEntity(SeatHold domain) {
        SeatHoldJpa entity = new SeatHoldJpa();
        entity.setId(domain.id());
        entity.setTripId(domain.tripId());
        entity.setSeat(domain.seat().getValue());
        entity.setUserId(domain.userId());
        entity.setExpiresAt(domain.expiresAt());
        return entity;
    }
}
