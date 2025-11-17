<<<<<<< Updated upstream:backend/buses-api/src/main/java/cl/venegas/buses_api/application/usecase/GetUserBookingsService.java
package cl.venegas.buses_api.application.usecase;
=======
package cl.app.application.usecase;
>>>>>>> Stashed changes:backend/src/main/java/cl/app/application/usecase/GetUserBookingsService.java

import java.util.List;

import org.springframework.stereotype.Service;

import cl.venegas.buses_api.domain.model.Booking;
import cl.venegas.buses_api.domain.port.BookingRepository;

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