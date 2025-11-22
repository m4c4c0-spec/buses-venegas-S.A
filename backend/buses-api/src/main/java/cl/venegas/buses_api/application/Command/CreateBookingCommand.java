package cl.venegas.buses_api.application.Command;

import cl.venegas.buses_api.domain.model.entity.Passenger;

public record CreateBookingCommand(
        Long tripId,
        Long userId,
        String seatNumber,
        Passenger passenger) {
}
