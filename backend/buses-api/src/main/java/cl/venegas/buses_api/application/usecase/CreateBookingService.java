package cl.venegas.buses_api.application.usecase;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.venegas.buses_api.application.exception.SeatAlreadyHeldException;
import cl.venegas.buses_api.domain.model.Booking;
import cl.venegas.buses_api.domain.model.BookingStatus;
import cl.venegas.buses_api.domain.model.Passenger;
import cl.venegas.buses_api.domain.model.SeatHold;
import cl.venegas.buses_api.domain.model.Trip;
import cl.venegas.buses_api.domain.model.valueobject.Money;
import cl.venegas.buses_api.domain.model.valueobject.SeatNumber;
import cl.venegas.buses_api.domain.port.BookingRepository;
import cl.venegas.buses_api.domain.port.SeatHoldRepository;
import cl.venegas.buses_api.domain.port.TripRepository;

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
    Trip trip = getTripOrThrow(tripId);
    validateSeatAvailability(tripId, seats);
    validatePassengerCount(seats, passengers);

    LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(HOLD_DURATION_MINUTES);
    createSeatHolds(userId, tripId, seats, expiresAt);

    return buildBooking(userId, tripId, seats, passengers, trip, expiresAt);
  }

  private Trip getTripOrThrow(Long tripId) {
    return tripRepository.findById(tripId)
        .orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado por la id " + tripId));
  }

  private void validateSeatAvailability(Long tripId, List<String> seats) {
    // Note: SeatHoldRepository likely still expects String seat numbers if it
    // hasn't been refactored.
    // Assuming SeatHold uses String for seatNumber for now as it wasn't in the plan
    // to refactor SeatHold entity deeply yet,
    // but we should check. If SeatHold uses String, we pass strings.
    List<SeatHold> existingHolds = seatHoldRepository.findByTripIdAndSeatNumberIn(tripId, seats);
    if (!existingHolds.isEmpty()) {
      throw new SeatAlreadyHeldException("Uno o mas asientos ya han sido agendados");
    }
  }

  private void validatePassengerCount(List<String> seats, List<Passenger> passengers) {
    if (passengers.size() != seats.size()) {
      throw new IllegalArgumentException("el numero de pasajeros no coincide con el de los asientos ");
    }
  }

  private void createSeatHolds(Long userId, Long tripId, List<String> seats, LocalDateTime expiresAt) {
    for (String seat : seats) {
      SeatHold hold = new SeatHold(null, tripId, seat, userId, expiresAt);
      seatHoldRepository.save(hold);
    }
  }

  private Booking buildBooking(Long userId, Long tripId, List<String> seats, List<Passenger> passengers, Trip trip,
      LocalDateTime expiresAt) {
    Booking booking = new Booking();
    booking.setUserId(userId);
    booking.setTripId(tripId);

    List<SeatNumber> seatNumbers = seats.stream().map(SeatNumber::new).toList();
    booking.setSeats(seatNumbers);

    booking.setPassengers(passengers);
    booking.setStatus(BookingStatus.PENDIENTE);

    Money totalAmount = trip.basePrice().multiply(seats.size());
    booking.setTotalAmount(totalAmount);

    booking.setCreatedAt(LocalDateTime.now());
    booking.setExpiresAt(expiresAt);

    return bookingRepository.save(booking);
  }
}