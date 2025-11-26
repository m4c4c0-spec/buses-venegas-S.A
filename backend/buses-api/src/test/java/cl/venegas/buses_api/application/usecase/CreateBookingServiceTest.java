package cl.venegas.buses_api.application.usecase;

import cl.venegas.buses_api.application.exception.SeatAlreadyHeldException;
import cl.venegas.buses_api.application.mapper.BookingMapper;
import cl.venegas.buses_api.domain.model.*;
import cl.venegas.buses_api.domain.model.valueobject.Email;
import cl.venegas.buses_api.domain.model.valueobject.Money;
import cl.venegas.buses_api.domain.model.valueobject.SeatNumber;
import cl.venegas.buses_api.domain.port.BookingRepository;
import cl.venegas.buses_api.domain.port.SeatHoldRepository;
import cl.venegas.buses_api.domain.port.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateBookingServiceTest {

        @Mock
        private BookingRepository bookingRepository;
        @Mock
        private TripRepository tripRepository;
        @Mock
        private SeatHoldRepository seatHoldRepository;
        @Mock
        private BookingMapper bookingMapper;

        @InjectMocks
        private CreateBookingService createBookingService;

        private Trip trip;
        private Booking booking;

        @BeforeEach
        void setUp() {
                trip = new Trip(1L, "Santiago", "Valparaiso", LocalDateTime.now().plusDays(1),
                                LocalDateTime.now().plusDays(1).plusHours(2), Money.of(10000));

                // Setup a dummy booking for return
                booking = Booking.create(1L, 1L, List.of(new SeatNumber("A1")),
                                List.of(new Passenger(1L, "John", "Doe", "DNI", "12345678",
                                                new Email("john@example.com"), "123456789")),
                                trip, LocalDateTime.now().plusMinutes(15));
                // Reflection or just assume ID is null for new booking, but repo returns one
                // with ID
                // Since Booking fields are private and no setters, we can't set ID easily
                // unless we use reflection or a constructor that allows it.
                // The factory method doesn't take ID. The constructor is public though.
                // Let's use the constructor for the "saved" booking to simulate DB ID
                // assignment.
                booking = new Booking(1L, 1L, 1L, List.of(new SeatNumber("A1")),
                                List.of(new Passenger(1L, "John", "Doe", "DNI", "12345678",
                                                new Email("john@example.com"), "123456789")),
                                BookingStatus.PENDIENTE, Money.of(10000), null, LocalDateTime.now(),
                                LocalDateTime.now().plusMinutes(15));
        }

        @Test
        @DisplayName("Debe crear una reserva correctamente")
        void shouldCreateBookingSuccessfully() {
                // 1. ARRANGE
                Long userId = 1L;
                Long tripId = 1L;
                List<String> seats = List.of("A1");
                List<Passenger> passengers = List.of(
                                new Passenger(1L, "John", "Doe", "DNI", "12345678", new Email("john@example.com"),
                                                "123456789"));

                when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));
                when(seatHoldRepository.findByTripIdAndSeatNumberIn(anyLong(), anyList()))
                                .thenReturn(Collections.emptyList());
                when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
                when(bookingMapper.toSeatNumbers(anyList())).thenReturn(List.of(new SeatNumber("A1")));

                // 2. ACT
                Booking result = createBookingService.handle(userId, tripId, seats, passengers);

                // 3. ASSERT
                assertNotNull(result);
                assertEquals(BookingStatus.PENDIENTE, result.getStatus());
                assertEquals(new BigDecimal("10000"), result.getTotalAmount().getAmount());

                verify(seatHoldRepository, times(1)).saveAll(anyList()); // Updated to saveAll
                verify(bookingRepository, times(1)).save(any(Booking.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción si el asiento ya está ocupado")
        void shouldThrowExceptionWhenSeatAlreadyHeld() {
                // 1. ARRANGE
                Long tripId = 1L;
                List<String> seats = List.of("A1");

                when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));
                when(seatHoldRepository.findByTripIdAndSeatNumberIn(anyLong(), anyList()))
                                .thenReturn(List.of(new SeatHold(1L, tripId, "A1", 2L, LocalDateTime.now())));

                List<Passenger> passengers = List.of(
                                new Passenger(1L, "John", "Doe", "DNI", "12345678", new Email("john@example.com"),
                                                "123456789"));

                // 2. ACT & ASSERT
                assertThrows(SeatAlreadyHeldException.class,
                                () -> createBookingService.handle(1L, tripId, seats, passengers));
        }

        @Test
        @DisplayName("Debe lanzar excepción si el viaje no existe")
        void shouldThrowExceptionWhenTripNotFound() {
                // 1. ARRANGE
                Long tripId = 999L;
                when(tripRepository.findById(tripId)).thenReturn(Optional.empty());

                List<Passenger> passengers = List.of(
                                new Passenger(1L, "John", "Doe", "DNI", "12345678", new Email("john@example.com"),
                                                "123456789"));

                // 2. ACT & ASSERT
                assertThrows(IllegalArgumentException.class,
                                () -> createBookingService.handle(1L, tripId, List.of("A1"), passengers));
        }

        @Test
        @DisplayName("Debe lanzar excepción si el número de pasajeros no coincide con los asientos")
        void shouldThrowExceptionWhenPassengerCountMismatch() {
                // 1. ARRANGE
                Long tripId = 1L;
                List<String> seats = List.of("A1", "A2");
                List<Passenger> passengers = List.of(
                                new Passenger(1L, "John", "Doe", "DNI", "12345678", new Email("john@example.com"),
                                                "123456789"));

                when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));
                when(seatHoldRepository.findByTripIdAndSeatNumberIn(anyLong(), anyList()))
                                .thenReturn(Collections.emptyList());

                // 2. ACT & ASSERT
                assertThrows(IllegalArgumentException.class,
                                () -> createBookingService.handle(1L, tripId, seats, passengers));
        }
}
