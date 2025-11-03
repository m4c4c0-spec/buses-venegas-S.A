package cl.venegas.buses_api.application.usecase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.venegas.buses_api.application.exception.SeatAlreadyHeldException;
import cl.venegas.buses_api.domain.model.Booking;
import cl.venegas.buses_api.domain.model.BookingStatus;
import cl.venegas.buses_api.domain.model.Passenger;
import cl.venegas.buses_api.domain.model.SeatHold;
import cl.venegas.buses_api.domain.model.Trip;
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

    Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado por la id " + tripId));

        List<SeatHold> existingHolds = seatHoldRepository.findByTripIdAndSeatIn(tripId, seats);
            if (!existingHolds.isEmpty()) {
        throw new SeatAlreadyHeldException("Uno o mas asientos ya han sido agendados");
    }


    if (passengers.size() != seats.size()) {
      throw new IllegalArgumentException("Number of passengers must match number of seats");
    }


    BigDecimal totalAmount = BigDecimal.valueOf(trip.basePriceClp()).multiply(BigDecimal.valueOf(seats.size()));


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
    booking.setTotalAmount(totalAmount);
    booking.setCreatedAt(LocalDateTime.now());
    booking.setExpiresAt(expiresAt);


    return bookingRepository.save(booking);
  }
}