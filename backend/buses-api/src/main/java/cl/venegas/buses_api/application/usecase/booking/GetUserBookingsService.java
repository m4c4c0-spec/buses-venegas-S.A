package cl.venegas.buses_api.application.usecase.booking;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cl.venegas.buses_api.domain.model.entity.Booking;
import cl.venegas.buses_api.domain.repository.BookingRepository;

@Service
public class GetUserBookingsService {

    private static final Logger log = LoggerFactory.getLogger(GetUserBookingsService.class);

    private final BookingRepository bookingRepository;

    public GetUserBookingsService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public List<Booking> execute(String userId) {
        log.info("Iniciando búsqueda de reservas: userId={}", userId);

        try {
            List<Booking> bookings = bookingRepository.findByUserId(Long.parseLong(userId));

            log.info("Búsqueda completada: userId={}, reservasEncontradas={}",
                    userId, bookings.size());

            return bookings;
        } catch (Exception e) {
            log.error("Error al buscar reservas: userId={}", userId, e);
            throw e;
        }
    }
}