package backend.src.main.java.cl.app.presentation.http.request;

import jakarta.validation.constraints.NotNull;

public record HoldRequest(@NotNull Long tripId, @NotNull String seat) {    
}