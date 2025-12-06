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
        java.util.List<Passenger> passengers = request.passengers().stream()
                .map(pReq -> new Passenger(
                        null,
                        pReq.firstName(),
                        pReq.lastName(),
                        pReq.documentType(),
                        pReq.documentNumber(),
                        pReq.email(),
                        pReq.phone()))
                .collect(java.util.stream.Collectors.toList());

        return new CreateBookingCommand(
                Long.parseLong(request.tripId()),
                Long.parseLong(request.userId()),
                request.seats(),
                passengers);
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