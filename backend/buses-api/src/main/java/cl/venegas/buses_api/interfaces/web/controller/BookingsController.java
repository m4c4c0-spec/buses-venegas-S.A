package cl.venegas.buses_api.interfaces.web.controller;

import cl.venegas.buses_api.application.Command.CreateBookingCommand;
import cl.venegas.buses_api.application.usecase.booking.CancelBookingService;
import cl.venegas.buses_api.application.usecase.booking.ConfirmBookingService;
import cl.venegas.buses_api.application.usecase.booking.CreateBookingUseCase;
import cl.venegas.buses_api.application.usecase.booking.GetUserBookingsService;
import cl.venegas.buses_api.domain.model.entity.Booking;
import cl.venegas.buses_api.interfaces.web.dto.request.CreateBookingRequest;
import cl.venegas.buses_api.interfaces.web.dto.response.BookingResponse;
import cl.venegas.buses_api.interfaces.web.mapper.BookingDTOMapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
@CrossOrigin(origins = "*")
public class BookingsController {

    private final CreateBookingUseCase createBookingUseCase;
    private final CancelBookingService cancelBookingService;
    private final ConfirmBookingService confirmBookingUseCase;
    private final GetUserBookingsService getUserBookingsUseCase;
    private final BookingDTOMapper mapper;

    public BookingsController(
            CreateBookingUseCase createBookingUseCase,
            CancelBookingService cancelBookingUseCase,
            ConfirmBookingService confirmBookingUseCase,
            GetUserBookingsService getUserBookingsUseCase,
            BookingDTOMapper mapper) {
        this.createBookingUseCase = createBookingUseCase;
        this.cancelBookingService = cancelBookingUseCase;
        this.confirmBookingUseCase = confirmBookingUseCase;
        this.getUserBookingsUseCase = getUserBookingsUseCase;
        this.mapper = mapper;
    }

    /**
     * POST /api/v1/bookings
     * Create un nuevo viaje
     */
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @RequestBody @Validated CreateBookingRequest request) {

        CreateBookingCommand command = mapper.toCreateCommand(request);

        Booking booking = createBookingUseCase.execute(command);

        BookingResponse response = mapper.toResponse(booking);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/v1/bookings/user/{userId}
     * obtiene todos los viajes del usuario
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingResponse>> getUserBookings(
            @PathVariable String userId) {

        List<Booking> bookings = getUserBookingsUseCase.execute(userId);

        List<BookingResponse> response = bookings.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/v1/bookings/{bookingId}/confirm
     * Confirma el viaje
     */
    @PutMapping("/{bookingId}/confirm")
    public ResponseEntity<BookingResponse> confirmBooking(
            @PathVariable String bookingId) {

        Booking booking = confirmBookingUseCase.execute(bookingId);

        BookingResponse response = mapper.toResponse(booking);

        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/v1/bookings/{bookingId}
     * cancela el viaje
     */
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable String bookingId) {

        cancelBookingService.execute(bookingId);

        return ResponseEntity.noContent().build();
    }
}