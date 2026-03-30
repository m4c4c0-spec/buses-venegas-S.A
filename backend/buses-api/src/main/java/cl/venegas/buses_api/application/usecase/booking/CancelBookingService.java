package cl.venegas.buses_api.application.usecase.booking;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.venegas.buses_api.domain.model.entity.Booking;
import cl.venegas.buses_api.domain.model.entity.SeatHold;
import cl.venegas.buses_api.domain.model.valueobject.BookingStatus;
import cl.venegas.buses_api.domain.repository.BookingRepository;
import cl.venegas.buses_api.domain.repository.SeatHoldRepository;

@Service
public class CancelBookingService {

  private static final Logger log = LoggerFactory.getLogger(CancelBookingService.class);

  private final BookingRepository bookingRepository;
  private final SeatHoldRepository seatHoldRepository;

  public CancelBookingService(BookingRepository bookingRepository,
      SeatHoldRepository seatHoldRepository) {
    this.bookingRepository = bookingRepository;
    this.seatHoldRepository = seatHoldRepository;
  }

  @Transactional
  public void execute(String bookingId) {
    log.info("Iniciando cancelación de reserva: bookingId={}", bookingId);

    Long id = Long.parseLong(bookingId);
    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Reserva no encontrada para cancelar: bookingId={}", bookingId);
          return new IllegalArgumentException("Reserva no encontrada: " + bookingId);
        });

    log.debug("Reserva encontrada: bookingId={}, status={}", bookingId, booking.getStatus());

    if (booking.getStatus() == BookingStatus.CANCELADO) {
      log.warn("Intento de cancelar reserva ya cancelada: bookingId={}", bookingId);
      throw new IllegalStateException("La reserva ya ha sido cancelada");
    }

    if (booking.getStatus() == BookingStatus.EXPIRADO) {
      log.warn("Intento de cancelar reserva expirada: bookingId={}", bookingId);
      throw new IllegalStateException("La reserva ha expirado y no puede ser cancelada");
    }

    booking.setStatus(BookingStatus.CANCELADO);
    bookingRepository.save(booking);

    log.debug("Liberando asientos retenidos: tripId={}, seats={}",
        booking.getTripId(), booking.getSeats());

    List<SeatHold> seatHolds = seatHoldRepository.findByTripIdAndSeatNumberIn(
        booking.getTripId(),
        booking.getSeats());

    if (!seatHolds.isEmpty()) {
      seatHoldRepository.deleteAll(seatHolds);
      log.debug("Liberados {} asientos", seatHolds.size());
    }

    log.info("Reserva cancelada exitosamente: bookingId={}, seatsLiberated={}",
        bookingId, seatHolds.size());
  }
}