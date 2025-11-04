package cl.venegas.buses_api.domain.model;

import java.time.LocalDate;

public record Bus(Long id, String plateNumber, String model, Integer totalSeats, cl.venegas.buses_api.domain.model.BusType type,
                    BusStatus status, LocalDate ultimaMantencion) {
}