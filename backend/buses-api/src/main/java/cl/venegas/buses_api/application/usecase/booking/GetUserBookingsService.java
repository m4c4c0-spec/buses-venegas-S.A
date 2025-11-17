package cl.venegas.buses_api.application.usecase.booking;

import java.util.List;

import org.springframework.stereotype.Service;

import cl.venegas.buses_api.domain.model.entity.Booking;
import cl.venegas.buses_api.domain.repository.BookingRepository;



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