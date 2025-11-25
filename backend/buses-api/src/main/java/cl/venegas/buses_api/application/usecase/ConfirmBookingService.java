package cl.venegas.buses_api.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.venegas.buses_api.domain.model.Booking;
import cl.venegas.buses_api.domain.model.BookingStatus;
import cl.venegas.buses_api.domain.port.BookingRepository;

@Service
public class ConfirmBookingService {

  private final BookingRepository bookingRepository;

  public ConfirmBookingService(BookingRepository bookingRepository) {
    this.bookingRepository = bookingRepository;
  }

  @Transactional
  public void handle(Long bookingId, String paymentReference) {
    Booking booking = bookingRepository.findById(bookingId)
        .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada por id: " + bookingId));

    booking.confirm(paymentReference);

    bookingRepository.save(booking);
  }

}
