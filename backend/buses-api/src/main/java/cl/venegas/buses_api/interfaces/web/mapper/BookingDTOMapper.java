package cl.venegas.buses_api.interfaces.web.mapper;

import org.springframework.stereotype.Component;

import cl.venegas.buses_api.application.Command.CreateBookingCommand;
import cl.venegas.buses_api.domain.model.entity.Booking;
import cl.venegas.buses_api.domain.model.entity.Passenger;
import cl.venegas.buses_api.interfaces.web.dto.request.CreateBookingRequest;
import cl.venegas.buses_api.interfaces.web.dto.request.PassengerRequest;
import cl.venegas.buses_api.interfaces.web.dto.response.BookingResponse;

@Component
public class BookingDTOMapper {

    public CreateBookingCommand toCreateCommand(CreateBookingRequest request) {
        PassengerRequest pReq = request.passenger();
        Passenger passenger = new Passenger(
                null,
                pReq.firstName(),
                pReq.lastName(),
                pReq.documentType(),
                pReq.documentNumber(),
                pReq.email(),
                pReq.phone());

        return new CreateBookingCommand(
                Long.parseLong(request.tripId()),
                Long.parseLong(request.userId()),
                request.seatNumber(),
                passenger);
    }

    public BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getTripId(),
                booking.getUserId(),
                booking.getSeats(),
                booking.getStatus(),
                booking.getTotalAmount(),
                booking.getCreatedAt(),
                booking.getExpiresAt());
    }
}