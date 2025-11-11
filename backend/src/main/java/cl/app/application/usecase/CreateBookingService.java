package backend.src.main.java.cl.app.application.usecase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.src.main.java.cl.app.application.exception.SeatAlreadyHeldException;
import backend.src.main.java.cl.app.domain.model.Booking;
import backend.src.main.java.cl.app.domain.model.BookingStatus;
import backend.src.main.java.cl.app.domain.model.Passenger;
import backend.src.main.java.cl.app.domain.model.SeatHold;
import backend.src.main.java.cl.app.domain.model.Trip;
import backend.src.main.java.cl.app.domain.port.BookingRepository;
import backend.src.main.java.cl.app.domain.port.SeatHoldRepository;
import backend.src.main.java.cl.app.domain.port.TripRepository;

@Service
public class CreateBookingService {

  private final BookingRepository bookingRepository;
  private final TripRepository tripRepository;
  private final SeatHoldRepository seatHoldRepository;

  private static final int HOLD_DURATION_MINUTES = 15;

  public CreateBookingService(BookingRepository bookingRepository,
                              TripRepository tripRepository,
                              SeatHoldRepository seatHoldRepository) {
    this.bookingRepository = bookingRepository;
    this.tripRepository = tripRepository;
    this.seatHoldRepository = seatHoldRepository;
  }

  @Transactional
  public Booking handle(Long userId, Long tripId, List<String> seats, List<Passenger> passengers) {

    Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado por la id " + tripId));


    List<SeatHold> existingHolds = seatHoldRepository.findByTripIdAndSeatNumberIn(tripId, seats);
    if (!existingHolds.isEmpty()) {
      throw new SeatAlreadyHeldException("Uno o mas asientos ya han sido agendados");
    }


    if (passengers.size() != seats.size()) {
      throw new IllegalArgumentException("el numero de pasajeros no coincide con el de los asientos ");
    }


    BigDecimal.valueOf(trip.basePriceClp());

    LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(HOLD_DURATION_MINUTES);
    List<SeatHold> seatHolds = new ArrayList<>();
    for (String seat : seats) {
     SeatHold hold = new SeatHold(null, tripId, seat, userId, expiresAt);
      seatHolds.add(seatHoldRepository.save(hold));
    }


    Booking booking = new Booking();
    booking.setUserId(userId);
    booking.setTripId(tripId);
    booking.setSeats(seats);
    booking.setPassengers(passengers);
    booking.setStatus(BookingStatus.PENDIENTE);
    BigDecimal totalAmount = null;
    booking.setTotalAmount(totalAmount);
    booking.setCreatedAt(LocalDateTime.now());
    booking.setExpiresAt(expiresAt);


    return bookingRepository.save(booking);
  }
}