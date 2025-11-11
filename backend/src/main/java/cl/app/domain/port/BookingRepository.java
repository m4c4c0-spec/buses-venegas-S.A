package backend.src.main.java.cl.app.domain.port;

import java.util.List;
import java.util.Optional;
import backend.src.main.java.cl.app.domain.model.Booking;
import backend.src.main.java.cl.app.domain.model.BookingStatus;

public interface BookingRepository {
    Booking save(Booking booking);
    Optional<Booking> findById(Long id);
    List<Booking> findByUserId(Long userId);
    List<Booking> findByTripId(Long tripId);
    List<Booking> findByStatus(BookingStatus status);
    void updateStatus(Long bookingId, BookingStatus newStatus);
    void delete(Booking booking);
}