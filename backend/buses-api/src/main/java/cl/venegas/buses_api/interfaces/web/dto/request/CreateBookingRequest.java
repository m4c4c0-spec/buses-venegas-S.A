package cl.venegas.buses_api.interfaces.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateBookingRequest(

        @NotBlank(message = "Id del viaje es requerido") String tripId,

        @NotBlank(message = "Id del usuario es requerido") String userId,

        @NotNull(message = "Lista de asientos es requerida") java.util.List<String> seats,

        @NotNull(message = "Lista de pasajeros es requerida") java.util.List<PassengerRequest> passengers) {
}