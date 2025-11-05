package cl.venegas.buses_api.presentation.http.request;

import jakarta.validation.constraints.NotNull;

public record HoldRequest(@NotNull Long tripId, @NotNull String seat) {
}