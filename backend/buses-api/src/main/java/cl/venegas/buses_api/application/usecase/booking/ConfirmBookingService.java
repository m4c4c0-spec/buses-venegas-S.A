package cl.venegas.buses_api.application.usecase.booking;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.venegas.buses_api.domain.model.entity.Booking;
import cl.venegas.buses_api.domain.model.valueobject.BookingStatus;
import cl.venegas.buses_api.domain.repository.BookingRepository;

@Service
public class ConfirmBookingService {

  private final BookingRepository bookingRepository;

  public ConfirmBookingService(BookingRepository bookingRepository) {
    this.bookingRepository = bookingRepository;
  }

  @Transactional
  public void handle(Long bookingId, String paymentReference) {
    Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("el viaje no ha sido encontrado por el id : " + bookingId));

    if (booking.getStatus() != BookingStatus.PENDIENTE) {
      throw new IllegalStateException("esta el viaje en estado de pendiente " + booking.getStatus());
    }


    if (booking.getExpiresAt() != null && LocalDateTime.now().isAfter(booking.getExpiresAt())) {
      booking.setStatus(BookingStatus.EXPIRADO);
      bookingRepository.save(booking);
      throw new IllegalStateException("el viaje ha expirado");
    }


    booking.setStatus(BookingStatus.CONFIRMADO);
    booking.setPaymentReference(paymentReference);


    bookingRepository.save(booking);
  }
}
