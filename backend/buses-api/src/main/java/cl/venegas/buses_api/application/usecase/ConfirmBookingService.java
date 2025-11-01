package main.java.cl.venegas.buses_api.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import main.java.cl.venegas.buses_api.domain.model.BookingStatus;
import main.java.cl.venegas.buses_api.domain.port.BookingRepository;

@Service
public class ConfirmBookingService {
  private final BookingRepository bookings;

  public ConfirmBookingService(BookingRepository bookings) {
    this.bookings = bookings;
  }

  @Transactional
  public void handle(Long bookingId, String paymentReference) {
    var booking = bookings.findById(bookingId)
        .orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado"));

    if (booking.status() != BookingStatus.PENDIENTE) {
      throw new IllegalStateException("El viaje ya ha sido confirmado");
    }

    // Se actualiza estado de la reserva
    bookings.updateStatus(bookingId, BookingStatus.CONFIRMADO);
  }
}
