package cl.venegas.buses_api.domain.repository;

import cl.venegas.buses_api.domain.model.entity.Booking;
import cl.venegas.buses_api.domain.model.valueobject.BookingStatus;


import java.util.List;
import java.util.Optional;

public interface BookingRepository {
    Booking save(Booking booking);
    Optional<Booking> findById(Long id);
    List<Booking> findByUserId(Long userId);
    List<Booking> findByTripId(Long tripId);
    List<Booking> findByStatus(BookingStatus status);
    void updateStatus(Long bookingId, BookingStatus newStatus);
    void delete(Booking booking);
}