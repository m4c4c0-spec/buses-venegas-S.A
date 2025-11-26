package cl.venegas.buses_api.application.usecase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
  public Booking handle(Long userId, Long tripId, List<String> seatStrings, List<Passenger> passengers) {
    List<SeatNumber> seats = seatStrings.stream()
        .map(SeatNumber::new)
        .collect(Collectors.toList());

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

  private void validateSeatAvailability(Long tripId, List<SeatNumber> seats) {
    List<SeatHold> existingHolds = seatHoldRepository.findByTripIdAndSeatNumberIn(tripId, seats);
    if (!existingHolds.isEmpty()) {
      throw new SeatAlreadyHeldException("Uno o mas asientos ya han sido agendados");
    }
  }

  private void validatePassengerCount(List<SeatNumber> seats, List<Passenger> passengers) {
    if (passengers.size() != seats.size()) {
      throw new IllegalArgumentException("el numero de pasajeros no coincide con el de los asientos ");
    }
  }

  private void createSeatHolds(Long userId, Long tripId, List<SeatNumber> seats, LocalDateTime expiresAt) {
    List<SeatHold> holds = seats.stream()
        .map(seat -> new SeatHold(null, tripId, seat, userId, expiresAt))
        .collect(Collectors.toList());
    seatHoldRepository.saveAll(holds);
  }

  private Booking buildBooking(Long userId, Long tripId, List<SeatNumber> seats, List<Passenger> passengers, Trip trip,
      LocalDateTime expiresAt) {

    Money totalAmount = trip.basePrice().multiply(seats.size());

    Booking booking = new Booking(
        null,
        userId,
        tripId,
        seats,
        passengers,
        BookingStatus.PENDIENTE,
        totalAmount,
        null,
        LocalDateTime.now(),
        expiresAt);

    return bookingRepository.save(booking);
  }
}