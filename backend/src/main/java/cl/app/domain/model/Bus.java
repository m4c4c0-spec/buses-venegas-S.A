package backend.src.main.java.cl.app.domain.model;

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