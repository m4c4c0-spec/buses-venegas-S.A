package cl.venegas.buses_api.application.Command;

import cl.venegas.buses_api.domain.model.entity.Passenger;

public record CreateBookingCommand(
                Long tripId,
                Long userId,
                java.util.List<String> seats,
                java.util.List<Passenger> passengers) {
}
