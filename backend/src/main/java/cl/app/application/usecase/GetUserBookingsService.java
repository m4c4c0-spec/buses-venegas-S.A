package backend.src.main.java.cl.app.application.usecase;

import java.util.List;

import backend.src.main.java.cl.app.domain.port.BookingRepository;
import org.springframework.stereotype.Service;

import backend.src.main.java.cl.app.domain.model.Booking;

@Service
public class GetUserBookingsService {

    private final BookingRepository bookingRepository;

    public GetUserBookingsService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public List<Booking> handle(Long userId) {
        //Se buscan todas las reservas del usuario
        return bookingRepository.findByUserId(userId);
    }
}