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
        String expiresAt) {
    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getUserId(),
                booking.getTripId(),
                booking.getSeats().stream().map(s -> s.getValue()).toList(),
                booking.getPassengers(),
                booking.getStatus(),
                booking.getTotalAmount().getAmount(),
                booking.getPaymentReference(),
                booking.getCreatedAt().toString(),
                booking.getExpiresAt().toString());
    }
}
