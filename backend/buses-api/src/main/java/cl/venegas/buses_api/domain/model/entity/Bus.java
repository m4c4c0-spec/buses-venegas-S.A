package cl.venegas.buses_api.domain.model.entity;

import cl.venegas.buses_api.domain.model.valueobject.BusStatus;
import cl.venegas.buses_api.domain.model.valueobject.BusType;
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