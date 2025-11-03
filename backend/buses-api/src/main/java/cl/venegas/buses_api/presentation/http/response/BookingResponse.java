package cl.venegas.buses_api.presentation.http.response;

import java.math.BigDecimal;
import java.util.List;

import cl.venegas.buses_api.domain.model.Booking;
import cl.venegas.buses_api.domain.model.BookingStatus;
import cl.venegas.buses_api.domain.model.Passenger;

public record BookingResponse(
        Long id,
        Long userId,
        Long tripId,
        List<String> seats,
        List<Passenger> passengers,
        BookingStatus status,
        BigDecimal totalAmount,
        String paymentReference,
        String createdAt,
        String expiresAt
) {
    public static BookingResponse from(Object booking) {
        return new BookingResponse(
                ((BookingResponse) booking).id(),
                ((BookingResponse) booking).userId(),
                ((BookingResponse) booking).tripId(),
                ((BookingResponse) booking).seats(),
                ((BookingResponse) booking).passengers(),
                ((BookingResponse) booking).status(),
                ((BookingResponse) booking).totalAmount(),
                ((BookingResponse) booking).paymentReference(),
                ((BookingResponse) booking).createdAt().toString(),
                ((BookingResponse) booking).expiresAt().toString()
        );
    }
}