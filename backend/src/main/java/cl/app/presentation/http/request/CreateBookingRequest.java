package backend.src.main.java.cl.app.presentation.http.request;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import backend.src.main.java.cl.app.domain.model.Passenger;

public record CreateBookingRequest(
        @NotNull Long tripId,
        @NotEmpty List<String> seats,
        @NotEmpty List<Passenger> passengers
) {}