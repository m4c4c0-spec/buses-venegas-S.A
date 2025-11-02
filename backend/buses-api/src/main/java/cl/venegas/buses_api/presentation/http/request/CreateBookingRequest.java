package cl.venegas.buses_api.presentation.http.request;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import cl.venegas.buses_api.domain.model.Passenger;

public record CreateBookingRequest(
        @NotNull Long tripId,
        @NotEmpty List<String> seats,
        @NotEmpty List<Passenger> passengers
) {}