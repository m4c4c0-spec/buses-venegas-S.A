package cl.venegas.buses_api.application.usecase;

import java.time.LocalDateTime;

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
            .orElseThrow(() -> new IllegalArgumentException("Booking not found with id: " + bookingId));

    if (booking.getStatus() != BookingStatus.PENDIENTE) {
      throw new IllegalStateException("Booking is not in PENDING status. Current status: " + booking.getStatus());
    }


    if (booking.getExpiresAt() != null && LocalDateTime.now().isAfter(booking.getExpiresAt())) {
      booking.setStatus(BookingStatus.EXPIRADO);
      bookingRepository.save(booking);
      throw new IllegalStateException("Booking has expired");
    }


    booking.setStatus(BookingStatus.CONFIRMADO);
    booking.setPaymentReference(paymentReference);


    bookingRepository.save(booking);
  }
}
