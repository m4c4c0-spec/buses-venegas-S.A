package cl.venegas.buses_api.domain.port;

import java.util.List;
import java.util.Optional;

import cl.venegas.buses_api.domain.model.Booking;
import cl.venegas.buses_api.domain.model.BookingStatus;

public interface BookingRepository {
  Booking save(Booking booking);
  Optional<Booking> findById(Long id);
  List<Booking> findByUserId(Long userId);
  List<Booking> findByTripId(Long tripId);
  List<Booking> findByStatus(BookingStatus status);
  void updateStatus(Long bookingId, BookingStatus newStatus);
  void delete(Booking booking);
}