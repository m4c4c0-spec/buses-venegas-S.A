package cl.venegas.buses_api.presentation.http.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cl.venegas.buses_api.application.usecase.GetUserBookingsService;
import cl.venegas.buses_api.presentation.http.request.CreateBookingRequest;
import cl.venegas.buses_api.presentation.http.response.BookingResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingsController {

    private final GetUserBookingsService getUserBookings;
    // Otros servicios comentados por ahora
    // private final CreateBookingService createBooking;
    // private final ConfirmBookingService confirmBooking;
    // private final CancelBookingService cancelBooking;

    public BookingsController(GetUserBookingsService getUserBookings) {
        this.getUserBookings = getUserBookings;
    }

    // @PostMapping
    // public ResponseEntity<BookingResponse> create(@Valid @RequestBody CreateBookingRequest request) {
    //   Long userId = 1L;
    //   var booking = createBooking.handle(userId, request.tripId(), request.seats(), request.passengers());
    //   return ResponseEntity.ok(BookingResponse.from(booking));
    // }

    // @PostMapping("/{bookingId}/confirm")
    // public ResponseEntity<Void> confirm(@PathVariable Long bookingId, @RequestParam String paymentReference) {
    //   confirmBooking.handle(bookingId, paymentReference);
    //   return ResponseEntity.ok().build();
    // }

    // @PostMapping("/{bookingId}/cancel")
    // public ResponseEntity<Void> cancel(@PathVariable Long bookingId) {
    //   Long userId = 1L;
    //   cancelBooking.handle(bookingId, userId);
    //   return ResponseEntity.ok().build();
    // }

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