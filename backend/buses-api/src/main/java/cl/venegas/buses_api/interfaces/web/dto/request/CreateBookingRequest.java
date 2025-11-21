package cl.venegas.buses_api.interfaces.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateBookingRequest(

                @NotBlank(message = "Id del viaje es requerido") String tripId,

                @NotBlank(message = "Id del usuario es requerido") String userId,

                @NotBlank(message = "Numero de asiento es requerido") String seatNumber,

                @NotNull(message = "Informacion del pasajero es requerida") PassengerRequest passenger) {
}