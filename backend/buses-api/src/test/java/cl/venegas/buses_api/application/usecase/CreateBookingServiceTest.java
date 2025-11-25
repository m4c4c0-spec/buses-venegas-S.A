package cl.venegas.buses_api.application.usecase;

import cl.venegas.buses_api.application.exception.SeatAlreadyHeldException;
import cl.venegas.buses_api.domain.model.*;
import cl.venegas.buses_api.domain.model.valueobject.Email;
import cl.venegas.buses_api.domain.model.valueobject.Money;
import cl.venegas.buses_api.domain.port.BookingRepository;
import cl.venegas.buses_api.domain.port.SeatHoldRepository;
import cl.venegas.buses_api.domain.port.TripRepository;
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
        @InjectMocks
        private CreateBookingService createBookingService;

        @Test
        @DisplayName("Debe crear una reserva correctamente")
        void shouldCreateBookingSuccessfully() {
                // 1. ARRANGE
                Long userId = 1L;
                Long tripId = 100L;
                List<String> seats = List.of("A1", "A2");
                List<Passenger> passengers = List.of(
                                new Passenger(1L, "John", "Doe", "DNI", "12345678", new Email("john@example.com"),
                                                "123456789"),
                                new Passenger(2L, "Jane", "Doe", "DNI", "87654321", new Email("jane@example.com"),
                                                "987654321"));

                Trip trip = new Trip(tripId, "Santiago", "Temuco", LocalDateTime.now(),
                                LocalDateTime.now().plusHours(5), Money.of(10000));

                when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));
                when(seatHoldRepository.findByTripIdAndSeatNumberIn(any(), any())).thenReturn(Collections.emptyList());
                when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArguments()[0]);

                // 2. ACT
                Booking result = createBookingService.handle(userId, tripId, seats, passengers);

                // 3. ASSERT
                assertNotNull(result);

                // Capturar el objeto guardado para verificar estado interno
                org.mockito.ArgumentCaptor<Booking> bookingCaptor = org.mockito.ArgumentCaptor.forClass(Booking.class);
                verify(bookingRepository).save(bookingCaptor.capture());
                Booking savedBooking = bookingCaptor.getValue();

                assertEquals(BookingStatus.PENDIENTE, savedBooking.getStatus());
                // 10000 * 2 asientos = 20000
                assertEquals(new BigDecimal("20000"), savedBooking.getTotalAmount().getAmount());
                assertNotNull(savedBooking.getExpiresAt());
                assertTrue(savedBooking.getExpiresAt().isAfter(LocalDateTime.now()));

                // Verificar que se crearon los bloqueos de asientos
                verify(seatHoldRepository, times(2)).save(any(SeatHold.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción si el asiento ya está ocupado")
        void shouldThrowExceptionWhenSeatAlreadyHeld() {
                // 1. ARRANGE
                Long tripId = 100L;
                List<String> seats = List.of("A1");

                when(tripRepository.findById(tripId)).thenReturn(Optional.of(new Trip(tripId, "Santiago", "Temuco",
                                LocalDateTime.now(), LocalDateTime.now().plusHours(5), Money.of(10000))));
                when(seatHoldRepository.findByTripIdAndSeatNumberIn(eq(tripId), eq(seats)))
                                .thenReturn(List.of(new SeatHold(1L, tripId, "A1", 2L, LocalDateTime.now())));
                // 2. ACT & ASSERT
                assertThrows(SeatAlreadyHeldException.class,
                                () -> createBookingService.handle(1L, tripId, seats, List.of(new Passenger(1L, "John",
                                                "Doe", "DNI", "12345678", new Email("john@example.com"),
                                                "123456789"))));
        }

        @Test
        @DisplayName("Debe lanzar excepción si el viaje no existe")
        void shouldThrowExceptionWhenTripNotFound() {
                // 1. ARRANGE
                Long tripId = 999L;
                when(tripRepository.findById(tripId)).thenReturn(Optional.empty());
                // 2. ACT & ASSERT
                assertThrows(IllegalArgumentException.class,
                                () -> createBookingService.handle(1L, tripId, List.of("A1"), List.of(new Passenger(1L,
                                                "John", "Doe", "DNI", "12345678", new Email("john@example.com"),
                                                "123456789"))));
        }

        @Test
        @DisplayName("Debe lanzar excepción si el número de pasajeros no coincide con los asientos")
        void shouldThrowExceptionWhenPassengerCountMismatch() {
                // 1. ARRANGE
                Long tripId = 100L;
                List<String> seats = List.of("A1", "A2");
                List<Passenger> passengers = List.of(
                                new Passenger(1L, "John", "Doe", "DNI", "12345678", new Email("john@example.com"),
                                                "123456789")); // Solo
                // 1
                // pasajero
                when(tripRepository.findById(tripId)).thenReturn(Optional.of(new Trip(tripId, "Santiago", "Temuco",
                                LocalDateTime.now(), LocalDateTime.now().plusHours(5), Money.of(10000))));
                when(seatHoldRepository.findByTripIdAndSeatNumberIn(any(), any())).thenReturn(Collections.emptyList());
                // 2. ACT & ASSERT
                assertThrows(IllegalArgumentException.class,
                                () -> createBookingService.handle(1L, tripId, seats, passengers));
        }
}
