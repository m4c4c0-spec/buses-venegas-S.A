package cl.app.domain.model.entity;

import cl.app.domain.model.entity.Bus;
import cl.app.domain.model.valueobject.BusStatus;
import cl.app.domain.model.valueobject.BusType;

import java.time.LocalDate;


public record Bus(
    Long id, 
    String plateNumber, 
    String model, 
    Integer totalSeats, 
    BusType type, 
    BusStatus status, 
    LocalDate ultimaMantencion
) {
}