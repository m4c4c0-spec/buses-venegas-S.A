package cl.venegas.buses_api.application.Command;

import java.util.List;

public record HoldSeatsCommand(
        Long tripId,
        List<String> seats,
        Long userId) {
}
