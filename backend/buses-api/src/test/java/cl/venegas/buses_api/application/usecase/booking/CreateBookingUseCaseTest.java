package cl.venegas.buses_api.application.usecase.booking;

import cl.venegas.buses_api.application.Command.CreateBookingCommand;
import cl.venegas.buses_api.domain.model.entity.Booking;
import cl.venegas.buses_api.domain.model.entity.Passenger;
import cl.venegas.buses_api.domain.model.entity.SeatHold;
import cl.venegas.buses_api.domain.model.entity.Trip;
import cl.venegas.buses_api.domain.model.valueobject.BookingStatus;
import cl.venegas.buses_api.domain.repository.BookingRepository;
import cl.venegas.buses_api.domain.repository.SeatHoldRepository;
import cl.venegas.buses_api.domain.repository.TripRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para CreateBookingUseCase
 * Adaptado de la versión PRE-migración (Hexagonal) a Onion Architecture
 * 
 * Cambios respecto a la versión anterior:
 * - Clase renombrada de CreateBookingService a CreateBookingUseCase
 * - Método renombrado de handle() a execute()
 * - Ahora recibe CreateBookingCommand en vez de parámetros individuales
 * - Ya no usa Value Objects Money y SeatNumber (usa primitivos)
 * - Trip.basePrice() ahora es Trip.basePriceClp() (Integer)
 * - Repositorios en domain.repository en vez de domain.port
 */
@ExtendWith(MockitoExtension.class)
class CreateBookingUseCaseTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private SeatHoldRepository seatHoldRepository;

    @InjectMocks
    private CreateBookingUseCase createBookingUseCase;

    @Test
    @DisplayName("Debe crear una reserva correctamente")
    void shouldCreateBookingSuccessfully() {
        // 1. ARRANGE
        Long userId = 1L;
        Long tripId = 100L;
        String seatNumber = "A1";
        Passenger passenger = new Passenger(1L, "John", "Doe", "DNI", "12345678", "john@example.com", "123456789");

        Trip trip = new Trip(tripId, "Santiago", "Temuco",
                LocalDateTime.now(), LocalDateTime.now().plusHours(5), 10000);

        CreateBookingCommand command = new CreateBookingCommand(tripId, userId, seatNumber, passenger);

        when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));
        when(seatHoldRepository.save(any(SeatHold.class))).thenAnswer(i -> i.getArguments()[0]);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArguments()[0]);

        // 2. ACT
        Booking result = createBookingUseCase.execute(command);

        // 3. ASSERT
        assertNotNull(result);

        // Capturar el objeto guardado para verificar estado interno
        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(bookingCaptor.capture());
        Booking savedBooking = bookingCaptor.getValue();

        assertEquals(BookingStatus.PENDIENTE, savedBooking.getStatus());
        assertEquals(userId, savedBooking.getUserId());
        assertEquals(tripId, savedBooking.getTripId());
        assertNotNull(savedBooking.getExpiresAt());
        assertTrue(savedBooking.getExpiresAt().isAfter(LocalDateTime.now()));

        // Verificar que se creó el bloqueo de asiento
        verify(seatHoldRepository, times(1)).save(any(SeatHold.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el viaje no existe")
    void shouldThrowExceptionWhenTripNotFound() {
        // 1. ARRANGE
        Long tripId = 999L;
        Long userId = 1L;
        Passenger passenger = new Passenger(1L, "John", "Doe", "DNI", "12345678", "john@example.com", "123456789");
        CreateBookingCommand command = new CreateBookingCommand(tripId, userId, "A1", passenger);

        when(tripRepository.findById(tripId)).thenReturn(Optional.empty());

        // 2. ACT & ASSERT
        assertThrows(IllegalArgumentException.class,
                () -> createBookingUseCase.execute(command));
    }

    @Test
    @DisplayName("Debe asignar correctamente el tripId y userId a la reserva")
    void shouldAssignCorrectTripAndUserToBooking() {
        // 1. ARRANGE
        Long userId = 42L;
        Long tripId = 123L;
        String seatNumber = "B5";
        Passenger passenger = new Passenger(2L, "Jane", "Smith", "PASSPORT", "AB123456", "jane@example.com",
                "555666777");

        Trip trip = new Trip(tripId, "Concepción", "Santiago",
                LocalDateTime.now(), LocalDateTime.now().plusHours(6), 15000);

        CreateBookingCommand command = new CreateBookingCommand(tripId, userId, seatNumber, passenger);

        when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));
        when(seatHoldRepository.save(any(SeatHold.class))).thenAnswer(i -> i.getArguments()[0]);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArguments()[0]);

        // 2. ACT
        Booking result = createBookingUseCase.execute(command);

        // 3. ASSERT
        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(bookingCaptor.capture());
        Booking savedBooking = bookingCaptor.getValue();

        assertEquals(userId, savedBooking.getUserId());
        assertEquals(tripId, savedBooking.getTripId());
        assertTrue(savedBooking.getSeats().contains(seatNumber));
        assertEquals(1, savedBooking.getPassengers().size());
    }

    @Test
    @DisplayName("Debe crear SeatHold con la expiración correcta")
    void shouldCreateSeatHoldWithCorrectExpiration() {
        // 1. ARRANGE
        Long userId = 1L;
        Long tripId = 100L;
        String seatNumber = "C3";
        Passenger passenger = new Passenger(1L, "Test", "User", "DNI", "11111111", "test@example.com", "111222333");

        Trip trip = new Trip(tripId, "Temuco", "Valdivia",
                LocalDateTime.now(), LocalDateTime.now().plusHours(2), 8000);

        CreateBookingCommand command = new CreateBookingCommand(tripId, userId, seatNumber, passenger);

        when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));
        when(seatHoldRepository.save(any(SeatHold.class))).thenAnswer(i -> i.getArguments()[0]);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArguments()[0]);

        // 2. ACT
        createBookingUseCase.execute(command);

        // 3. ASSERT
        ArgumentCaptor<SeatHold> holdCaptor = ArgumentCaptor.forClass(SeatHold.class);
        verify(seatHoldRepository).save(holdCaptor.capture());
        SeatHold savedHold = holdCaptor.getValue();

        assertEquals(tripId, savedHold.tripId());
        assertEquals(seatNumber, savedHold.seat());
        assertEquals(userId, savedHold.userId());
        assertNotNull(savedHold.expiresAt());
        // Verificar que expira en aproximadamente 15 minutos
        assertTrue(savedHold.expiresAt().isAfter(LocalDateTime.now().plusMinutes(14)));
        assertTrue(savedHold.expiresAt().isBefore(LocalDateTime.now().plusMinutes(16)));
    }
}
