package backend.src.main.java.cl.app.application.usecase;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.src.main.java.cl.app.domain.model.Booking;
import backend.src.main.java.cl.app.domain.model.BookingStatus;
import backend.src.main.java.cl.app.domain.model.SeatHold;
import backend.src.main.java.cl.app.domain.port.BookingRepository;
import backend.src.main.java.cl.app.domain.port.SeatHoldRepository;

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
  public void handle(Long bookingId, Long userId) {
    Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("El viaje ha sido cancelado" + bookingId));

    if (!booking.getUserId().equals(userId)) {
      throw new IllegalArgumentException("el viaje no pertence al usuario, ya que es un id distinto" + userId);
    }

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
            booking.getSeats()
    );

    if (!seatHolds.isEmpty()) {
      seatHoldRepository.deleteAll(seatHolds);
    }
  }
}