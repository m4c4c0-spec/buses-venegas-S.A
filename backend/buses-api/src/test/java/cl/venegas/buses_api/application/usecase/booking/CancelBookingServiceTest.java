package cl.venegas.buses_api.application.usecase.booking;

import cl.venegas.buses_api.domain.model.entity.Booking;
import cl.venegas.buses_api.domain.model.entity.SeatHold;
import cl.venegas.buses_api.domain.model.valueobject.BookingStatus;
import cl.venegas.buses_api.domain.repository.BookingRepository;
import cl.venegas.buses_api.domain.repository.SeatHoldRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para CancelBookingService
 * Adaptado de la versión PRE-migración (Hexagonal) a Onion Architecture
 * 
 * Cambios respecto a la versión anterior:
 * - Paquete cambió de application.usecase a application.usecase.booking
 * - Método cambió de handle(Long bookingId, Long userId) a execute(String
 * bookingId)
 * - Ya NO recibe userId como parámetro (no valida propietario)
 * - Ya NO tiene validación de usuario propietario
 * - Repositorios en domain.repository en vez de domain.port
 * - SeatHold en domain.model.entity
 * - seats ahora es List<String> en vez de List<SeatNumber>
 */

@ExtendWith(MockitoExtension.class)
class CancelBookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private SeatHoldRepository seatHoldRepository;

    @InjectMocks
    private CancelBookingService cancelBookingService;

    @Test
    @DisplayName("Debe cancelar la reserva exitosamente y liberar asientos")
    void shouldCancelBookingSuccessfully() {
        // 1. ARRANGE
        Long bookingId = 1L;
        String bookingIdStr = "1";
        Long tripId = 100L;
        Long userId = 10L;
        List<String> seats = List.of("A1", "A2");

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setUserId(userId);
        booking.setTripId(tripId);
        booking.setSeats(seats);
        booking.setStatus(BookingStatus.PENDIENTE);

        List<SeatHold> seatHolds = List.of(
                new SeatHold(1L, tripId, "A1", userId, LocalDateTime.now().plusMinutes(10)),
                new SeatHold(2L, tripId, "A2", userId, LocalDateTime.now().plusMinutes(10)));

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(seatHoldRepository.findByTripIdAndSeatNumberIn(tripId, seats)).thenReturn(seatHolds);

        // 2. ACT
        cancelBookingService.execute(bookingIdStr);

        // 3. ASSERT
        assertEquals(BookingStatus.CANCELADO, booking.getStatus());
        verify(bookingRepository).save(booking);
        verify(seatHoldRepository).deleteAll(seatHolds);
    }

    @Test
    @DisplayName("Debe lanzar excepción si la reserva no existe")
    void shouldThrowExceptionWhenBookingNotFound() {
        // 1. ARRANGE
        Long bookingId = 999L;
        String bookingIdStr = "999";

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        // 2. ACT & ASSERT
        assertThrows(IllegalArgumentException.class,
                () -> cancelBookingService.execute(bookingIdStr));
    }

    @Test
    @DisplayName("Debe lanzar excepción si la reserva ya está cancelada")
    void shouldThrowExceptionWhenBookingAlreadyCancelled() {
        // 1. ARRANGE
        Long bookingId = 1L;
        String bookingIdStr = "1";

        Booking booking = new Booking();
        booking.setStatus(BookingStatus.CANCELADO);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // 2. ACT & ASSERT
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> cancelBookingService.execute(bookingIdStr));

        assertTrue(exception.getMessage().toLowerCase().contains("cancelad"));
    }

    @Test
    @DisplayName("Debe lanzar excepción si la reserva ha expirado")
    void shouldThrowExceptionWhenBookingExpired() {
        // 1. ARRANGE
        Long bookingId = 1L;
        String bookingIdStr = "1";

        Booking booking = new Booking();
        booking.setStatus(BookingStatus.EXPIRADO);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // 2. ACT & ASSERT
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> cancelBookingService.execute(bookingIdStr));

        assertTrue(exception.getMessage().toLowerCase().contains("expir"));
    }

    @Test
    @DisplayName("Debe cancelar reserva incluso sin holds asociados")
    void shouldCancelBookingWithoutSeatHolds() {
        // 1. ARRANGE
        Long bookingId = 1L;
        String bookingIdStr = "1";
        Long tripId = 100L;
        List<String> seats = List.of("A1");

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setTripId(tripId);
        booking.setSeats(seats);
        booking.setStatus(BookingStatus.PENDIENTE);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(seatHoldRepository.findByTripIdAndSeatNumberIn(tripId, seats)).thenReturn(Collections.emptyList());

        // 2. ACT
        cancelBookingService.execute(bookingIdStr);

        // 3. ASSERT
        assertEquals(BookingStatus.CANCELADO, booking.getStatus());
        verify(bookingRepository).save(booking);
        // No debe llamar deleteAll si la lista está vacía
        verify(seatHoldRepository, never()).deleteAll(anyList());
    }

    @Test
    @DisplayName("Debe cancelar reserva confirmada correctamente")
    void shouldCancelConfirmedBooking() {
        // 1. ARRANGE
        Long bookingId = 1L;
        String bookingIdStr = "1";
        Long tripId = 100L;
        List<String> seats = List.of("B3");

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setTripId(tripId);
        booking.setSeats(seats);
        booking.setStatus(BookingStatus.CONFIRMADO);

        List<SeatHold> seatHolds = List.of(
                new SeatHold(1L, tripId, "B3", 1L, LocalDateTime.now().plusMinutes(10)));

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(seatHoldRepository.findByTripIdAndSeatNumberIn(tripId, seats)).thenReturn(seatHolds);

        // 2. ACT
        cancelBookingService.execute(bookingIdStr);

        // 3. ASSERT
        assertEquals(BookingStatus.CANCELADO, booking.getStatus());
        verify(bookingRepository).save(booking);
        verify(seatHoldRepository).deleteAll(seatHolds);
    }

    @Test
    @DisplayName("Debe parsear correctamente el bookingId de String a Long")
    void shouldParseBookingIdCorrectly() {
        // 1. ARRANGE
        String bookingIdStr = "54321";
        Long expectedId = 54321L;
        Long tripId = 100L;
        List<String> seats = List.of("C1");

        Booking booking = new Booking();
        booking.setId(expectedId);
        booking.setTripId(tripId);
        booking.setSeats(seats);
        booking.setStatus(BookingStatus.PENDIENTE);

        when(bookingRepository.findById(expectedId)).thenReturn(Optional.of(booking));
        when(seatHoldRepository.findByTripIdAndSeatNumberIn(eq(tripId), eq(seats))).thenReturn(Collections.emptyList());

        // 2. ACT
        cancelBookingService.execute(bookingIdStr);

        // 3. ASSERT
        verify(bookingRepository).findById(expectedId);
    }

    @Test
    @DisplayName("Debe liberar múltiples asientos correctamente")
    void shouldReleaseMultipleSeats() {
        // 1. ARRANGE
        Long bookingId = 1L;
        String bookingIdStr = "1";
        Long tripId = 100L;
        Long userId = 10L;
        List<String> seats = List.of("A1", "A2", "A3", "A4");

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setTripId(tripId);
        booking.setSeats(seats);
        booking.setStatus(BookingStatus.PENDIENTE);

        List<SeatHold> seatHolds = List.of(
                new SeatHold(1L, tripId, "A1", userId, LocalDateTime.now().plusMinutes(10)),
                new SeatHold(2L, tripId, "A2", userId, LocalDateTime.now().plusMinutes(10)),
                new SeatHold(3L, tripId, "A3", userId, LocalDateTime.now().plusMinutes(10)),
                new SeatHold(4L, tripId, "A4", userId, LocalDateTime.now().plusMinutes(10)));

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(seatHoldRepository.findByTripIdAndSeatNumberIn(tripId, seats)).thenReturn(seatHolds);

        // 2. ACT
        cancelBookingService.execute(bookingIdStr);

        // 3. ASSERT
        verify(seatHoldRepository).deleteAll(seatHolds);
        assertEquals(4, seatHolds.size());
    }
}
