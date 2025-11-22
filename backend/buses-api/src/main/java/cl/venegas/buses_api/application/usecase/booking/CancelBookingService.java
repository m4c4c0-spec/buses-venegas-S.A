package cl.venegas.buses_api.application.usecase.booking;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.venegas.buses_api.domain.model.entity.Booking;
import cl.venegas.buses_api.domain.model.entity.SeatHold;
import cl.venegas.buses_api.domain.model.valueobject.BookingStatus;
import cl.venegas.buses_api.domain.repository.BookingRepository;
import cl.venegas.buses_api.domain.repository.SeatHoldRepository;

@Service
public class CancelBookingService {

  private final BookingRepository bookingRepository;
  private final SeatHoldRepository seatHoldRepository;

  public CancelBookingService(BookingRepository bookingRepository,
      SeatHoldRepository seatHoldRepository) {
    this.bookingRepository = bookingRepository;
    this.seatHoldRepository = seatHoldRepository;
  }

  @Transactional
  public void execute(String bookingId) {
    Long id = Long.parseLong(bookingId);
    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("El viaje ha sido cancelado" + bookingId));

    if (booking.getStatus() == BookingStatus.CANCELADO) {
      throw new IllegalStateException("el viaje ya ha sido cancelado");
    }

    if (booking.getStatus() == BookingStatus.EXPIRADO) {
      throw new IllegalStateException("este viaje no puede ser cancelado ya que ha expirado");
    }

    booking.setStatus(BookingStatus.CANCELADO);
    bookingRepository.save(booking);

    List<SeatHold> seatHolds = seatHoldRepository.findByTripIdAndSeatNumberIn(
        booking.getTripId(),
        booking.getSeats());

    if (!seatHolds.isEmpty()) {
      seatHoldRepository.deleteAll(seatHolds);
    }
  }
}