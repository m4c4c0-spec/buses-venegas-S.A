package cl.app.domain.repository;

import cl.app.domain.model.entity.Booking;
import java.util.List;
import java.util.Optional;


public interface BookingRepository {
    Booking save(Booking booking);
    Optional<Booking> findById(Long id);
    List<Booking> findByUserId(Long userId);
    List<Booking> findByTripId(Long tripId);
    List<Booking> findByStatus(cl.app.domain.model.valueobject.BookingStatus status);
    void updateStatus(Long bookingId, cl.app.domain.model.valueobject.BookingStatus newStatus);
    void delete(Booking booking);
}