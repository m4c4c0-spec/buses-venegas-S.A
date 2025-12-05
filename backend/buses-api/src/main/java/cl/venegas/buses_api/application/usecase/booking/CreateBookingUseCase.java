package cl.venegas.buses_api.application.usecase.booking;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.venegas.buses_api.application.Command.CreateBookingCommand;
import cl.venegas.buses_api.domain.model.entity.Booking;
import cl.venegas.buses_api.domain.model.entity.Passenger;
import cl.venegas.buses_api.domain.model.entity.SeatHold;
import cl.venegas.buses_api.domain.model.entity.Trip;
import cl.venegas.buses_api.domain.model.valueobject.BookingStatus;
import cl.venegas.buses_api.domain.repository.BookingRepository;
import cl.venegas.buses_api.domain.repository.SeatHoldRepository;
import cl.venegas.buses_api.domain.repository.TripRepository;

@Service
@Slf4j
public class CreateBookingUseCase {

  private final BookingRepository bookingRepository;
  private final TripRepository tripRepository;
  private final SeatHoldRepository seatHoldRepository;

  private static final int HOLD_DURATION_MINUTES = 15;

  public CreateBookingUseCase(BookingRepository bookingRepository,
      TripRepository tripRepository,
      SeatHoldRepository seatHoldRepository) {
    this.bookingRepository = bookingRepository;
    this.tripRepository = tripRepository;
    this.seatHoldRepository = seatHoldRepository;
  }

  @Transactional
  public Booking execute(CreateBookingCommand command) {
    log.info("Iniciando creación de reserva: tripId={}, userId={}, seatNumber={}",
        command.tripId(), command.userId(), command.seatNumber());

    Long tripId = command.tripId();
    Long userId = command.userId();
    List<String> seats = List.of(command.seatNumber());
    List<Passenger> passengers = List.of(command.passenger());

    Trip trip = tripRepository.findById(tripId)
        .orElseThrow(() -> {
          log.error("Viaje no encontrado: tripId={}", tripId);
          return new IllegalArgumentException("Viaje no encontrado por la id " + tripId);
        });

    if (passengers.size() != seats.size()) {
      log.error("Mismatch pasajeros/asientos: pasajeros={}, asientos={}", passengers.size(), seats.size());
      throw new IllegalArgumentException("el numero de pasajeros no coincide con el de los asientos ");
    }

    BigDecimal.valueOf(trip.basePriceClp());

    LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(HOLD_DURATION_MINUTES);
    List<SeatHold> seatHolds = new ArrayList<>();
    log.debug("Reteniendo asientos: {}", seats);
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

    // Calculate total amount
    BigDecimal pricePerSeat = BigDecimal.valueOf(trip.basePriceClp());
    BigDecimal totalAmount = pricePerSeat.multiply(BigDecimal.valueOf(seats.size()));
    booking.setTotalAmount(totalAmount);

    booking.setCreatedAt(LocalDateTime.now());
    booking.setExpiresAt(expiresAt);

    Booking savedBooking = bookingRepository.save(booking);
    log.info("Reserva creada exitosamente: bookingId={}, status={}, expiresAt={}",
        savedBooking.getId(), savedBooking.getStatus(), savedBooking.getExpiresAt());

    return savedBooking;
  }
}