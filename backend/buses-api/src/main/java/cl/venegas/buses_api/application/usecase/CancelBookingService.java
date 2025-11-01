package main.java.cl.venegas.buses_api.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import main.java.cl.venegas.buses_api.domain.model.BookingStatus;
import main.java.cl.venegas.buses_api.domain.port.BookingRepository;

@Service
public class CancelBookingService {
  private final BookingRepository bookings;

  public CancelBookingService(BookingRepository bookings) {
    this.bookings = bookings;
  }

  @Transactional
  public void handle(Long bookingId, Long userId) {
    var booking = bookings.findById(bookingId)
        .orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado "));

    if (!booking.userId().equals(userId)) {
      throw new IllegalStateException("Este Viaje no pertenece al usuario");
    }

    if (booking.status() == BookingStatus.CANCELADO || 
        booking.status() == BookingStatus.EXPIRADO) {
      throw new IllegalStateException("El viaje ya ha sido cancelo o expiro");
    }

    bookings.updateStatus(bookingId, BookingStatus.CANCELADO);
  }
}