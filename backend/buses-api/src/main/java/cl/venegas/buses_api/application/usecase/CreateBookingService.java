package cl.venegas.buses_api.application.usecase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.venegas.buses_api.application.exception.SeatAlreadyHeldException;
import cl.venegas.buses_api.application.mapper.BookingMapper;
import cl.venegas.buses_api.domain.model.Booking;
import cl.venegas.buses_api.domain.model.Passenger;
import cl.venegas.buses_api.domain.model.SeatHold;
import cl.venegas.buses_api.domain.model.Trip;
import cl.venegas.buses_api.domain.model.valueobject.SeatNumber;
import cl.venegas.buses_api.domain.port.BookingRepository;
import cl.venegas.buses_api.domain.port.SeatHoldRepository;
import cl.venegas.buses_api.domain.port.TripRepository;

@Service
public class CreateBookingService {

    private final BookingRepository bookingRepository;
    private final TripRepository tripRepository;
    private final SeatHoldRepository seatHoldRepository;
    private final BookingMapper bookingMapper;

    private static final int HOLD_DURATION_MINUTES = 15;

    public CreateBookingService(BookingRepository bookingRepository,
            TripRepository tripRepository,
            SeatHoldRepository seatHoldRepository,
            BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.tripRepository = tripRepository;
        this.seatHoldRepository = seatHoldRepository;
        this.bookingMapper = bookingMapper;
    }

    @Transactional
    public Booking handle(Long userId, Long tripId, List<String> seats, List<Passenger> passengers) {
        Trip trip = getTripOrThrow(tripId);
        validateSeatAvailability(tripId, seats);
        validatePassengerCount(seats, passengers);

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(HOLD_DURATION_MINUTES);
        createSeatHolds(userId, tripId, seats, expiresAt);

        List<SeatNumber> seatNumbers = bookingMapper.toSeatNumbers(seats);
        Booking booking = Booking.create(userId, tripId, seatNumbers, passengers, trip, expiresAt);

        return bookingRepository.save(booking);
    }

    private Trip getTripOrThrow(Long tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Viaje no encontrado por la id " + tripId));
    }

    private void validateSeatAvailability(Long tripId, List<String> seats) {
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
        List<SeatHold> holds = new ArrayList<>();
        for (String seat : seats) {
            holds.add(new SeatHold(null, tripId, seat, userId, expiresAt));
        }
        seatHoldRepository.saveAll(holds);
    }
}