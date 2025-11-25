package cl.venegas.buses_api.application.usecase;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.venegas.buses_api.domain.model.Booking;
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
        .orElseThrow(() -> new IllegalArgumentException("El viaje ha sido cancelado" + bookingId));

    // Delegate validation and state change to the domain entity
    booking.cancel(userId);

    bookingRepository.save(booking);

    List<SeatHold> seatHolds = seatHoldRepository.findByTripIdAndSeatNumberIn(
        booking.getTripId(),
        booking.getSeats().stream().map(cl.venegas.buses_api.domain.model.valueobject.SeatNumber::getValue).toList());

    if (!seatHolds.isEmpty()) {
      seatHoldRepository.deleteAll(seatHolds);
    }
  }
}