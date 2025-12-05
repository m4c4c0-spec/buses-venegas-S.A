package cl.venegas.buses_api.interfaces.web.dto.request;

import cl.venegas.buses_api.domain.model.valueobject.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record ProcessPaymentRequest(
        @NotNull(message = "El ID de reserva es requerido") Long bookingId,

        @NotNull(message = "El método de pago es requerido") PaymentMethod method) {
}
