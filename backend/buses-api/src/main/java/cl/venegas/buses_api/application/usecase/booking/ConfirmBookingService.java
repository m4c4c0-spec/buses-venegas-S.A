package cl.venegas.buses_api.application.usecase.booking;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.venegas.buses_api.domain.model.entity.Booking;
import cl.venegas.buses_api.domain.model.valueobject.BookingStatus;
import cl.venegas.buses_api.domain.repository.BookingRepository;

@Service
public class ConfirmBookingService {

  private static final Logger log = LoggerFactory.getLogger(ConfirmBookingService.class);

  private final BookingRepository bookingRepository;

  public ConfirmBookingService(BookingRepository bookingRepository) {
    this.bookingRepository = bookingRepository;
  }

  @Transactional
  public Booking execute(String bookingId) {
    log.info("Iniciando confirmación de reserva: bookingId={}", bookingId);

    Long id = Long.parseLong(bookingId);
    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Reserva no encontrada: bookingId={}", bookingId);
          return new IllegalArgumentException("Reserva no encontrada: " + bookingId);
        });

    log.debug("Reserva encontrada: bookingId={}, status={}", bookingId, booking.getStatus());
    log.debug("Validando estado de reserva: bookingId={}, status={}", bookingId, booking.getStatus());

    if (booking.getStatus() != BookingStatus.PENDIENTE) {
      log.warn("Reserva no está en estado PENDIENTE: bookingId={}, status={}",
          bookingId, booking.getStatus());
      throw new IllegalStateException("Reserva no está pendiente: " + booking.getStatus());
    }

    log.debug("Validando expiración: bookingId={}, expiresAt={}", bookingId, booking.getExpiresAt());

    if (booking.getExpiresAt() != null && LocalDateTime.now().isAfter(booking.getExpiresAt())) {
      log.warn("Reserva expirada: bookingId={}, expiresAt={}", bookingId, booking.getExpiresAt());
      booking.setStatus(BookingStatus.EXPIRADO);
      bookingRepository.save(booking);
      throw new IllegalStateException("Reserva expirada");
    }

    booking.setStatus(BookingStatus.CONFIRMADO);
    Booking savedBooking = bookingRepository.save(booking);

    log.info("Reserva confirmada exitosamente: bookingId={}, newStatus={}",
        bookingId, savedBooking.getStatus());

    return savedBooking;
  }
}
