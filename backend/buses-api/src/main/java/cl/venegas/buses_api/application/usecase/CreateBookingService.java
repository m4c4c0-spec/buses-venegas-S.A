package main.java.cl.venegas.buses_api.application.usecase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.venegas.buses_api.domain.port.SeatHoldRepository;
import main.java.cl.venegas.buses_api.domain.model.Booking;
import main.java.cl.venegas.buses_api.domain.model.BookingStatus;
import main.java.cl.venegas.buses_api.domain.model.Passenger;
import main.java.cl.venegas.buses_api.domain.port.BookingRepository;

@Service
public class CreateBookingService {
  private final BookingRepository bookings;
  private final TripRepository trips;
  private final SeatHoldRepository seatHolds;
  
  private static final int BOOKING_EXPIRY_MINUTES = 15;

  public CreateBookingService(BookingRepository bookings, 
                             TripRepository trips,
                             SeatHoldRepository seatHolds) {
    this.bookings = bookings;
    this.seatHolds = seatHolds;
  }

  @Transactional
  public Booking handle(Long userId, Long tripId, List<String> seats, List<Passenger> passengers) {
    // Validar que el viaje existe
    var trip = trips.findById(tripId)
        .orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado"));

    // Validar que la cantidad de pasajeros coincide con los asientos
    if (seats.size() != passengers.size()) {
      throw new IllegalArgumentException("Number of passengers must match number of seats");
    }

    // Retener los asientos
    for (String seat : seats) {
      seatHolds.hold(tripId, seat, userId, LocalDateTime.now().plusMinutes(10));
    }

    // Calcular precio total
    BigDecimal totalAmount = BigDecimal.valueOf(trip.basePriceClp())
        .multiply(BigDecimal.valueOf(seats.size()));

    // Crear la reserva
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
        LocalDateTime.now().plusMinutes(BOOKING_EXPIRY_MINUTES)
    );

    return bookings.save(booking);
  }
}
