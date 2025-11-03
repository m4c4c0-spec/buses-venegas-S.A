package cl.venegas.buses_api.application.usecase;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.venegas.buses_api.domain.model.Booking;
import cl.venegas.buses_api.domain.model.BookingStatus;
import cl.venegas.buses_api.domain.model.SeatHold;
import cl.venegas.buses_api.domain.port.BookingRepository;
import cl.venegas.buses_api.domain.port.SeatHoldRepository;

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
            .orElseThrow(() -> new IllegalArgumentException("Booking not found with id: " + bookingId));

    if (!booking.getUserId().equals(userId)) {
      throw new IllegalArgumentException("Booking does not belong to user with id: " + userId);
    }

    if (booking.getStatus() == BookingStatus.CANCELADO) {
      throw new IllegalStateException("Booking is already cancelled");
    }

    if (booking.getStatus() == BookingStatus.EXPIRADO) {
      throw new IllegalStateException("Cannot cancel an expired booking");
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