package cl.venegas.buses_api.presentation.http.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.venegas.buses_api.application.usecase.CancelBookingService;
import cl.venegas.buses_api.application.usecase.ConfirmBookingService;
import cl.venegas.buses_api.application.usecase.CreateBookingService;
import cl.venegas.buses_api.application.usecase.GetUserBookingsService;
import cl.venegas.buses_api.presentation.http.request.CreateBookingRequest;
import cl.venegas.buses_api.presentation.http.response.BookingResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingsController {
    private final CreateBookingService createBooking;
    private final ConfirmBookingService confirmBooking;
    private final CancelBookingService cancelBooking;
    private final GetUserBookingsService getUserBookings;

    public BookingsController(CreateBookingService createBooking,
                              ConfirmBookingService confirmBooking,
                              CancelBookingService cancelBooking,
                              GetUserBookingsService getUserBookings) {
        this.createBooking = createBooking;
        this.confirmBooking = confirmBooking;
        this.cancelBooking = cancelBooking;
        this.getUserBookings = getUserBookings;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody CreateBookingRequest request) {
        Long userId = 1L;

        var booking = createBooking.handle(
                userId,
                request.tripId(),
                request.seats(),
                request.passengers()
        );

        return ResponseEntity.ok(BookingResponse.from(booking));
    }

    @PostMapping("/{bookingId}/confirm")
    public ResponseEntity<Void> confirm(@PathVariable Long bookingId,
                                        @RequestParam String paymentReference) {
        confirmBooking.handle(bookingId, paymentReference);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long bookingId) {
        Long userId = 1L;

        cancelBooking.handle(bookingId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponse>> getMyBookings() {
        Long userId = 1L;
        var bookings = getUserBookings.handle(userId);
        return ResponseEntity.ok(
                bookings.stream()
                        .map(BookingResponse::from)
                        .toList()
        );
    }
}