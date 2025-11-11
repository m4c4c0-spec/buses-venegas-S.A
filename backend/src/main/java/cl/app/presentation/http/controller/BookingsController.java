package backend.src.main.java.cl.app.presentation.http.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import backend.src.main.java.cl.app.application.usecase.GetUserBookingsService;
import backend.src.main.java.cl.app.presentation.http.response.BookingResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingsController {

    private final GetUserBookingsService getUserBookings;

    public BookingsController(GetUserBookingsService getUserBookings) {
        this.getUserBookings = getUserBookings;
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