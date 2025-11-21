package cl.venegas.buses_api.interfaces.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record HoldRequest(

                @NotBlank(message = "Id del viaje es requerido") String tripId,

                @NotBlank(message = "Id del usuario es requerido") String userId,

                @NotEmpty(message = "Al menos un asiento debe ser seleccionado") List<String> seatNumbers) {
}
