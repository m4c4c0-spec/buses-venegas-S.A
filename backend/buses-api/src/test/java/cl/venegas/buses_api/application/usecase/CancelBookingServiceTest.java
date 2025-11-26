package cl.venegas.buses_api.application.usecase;

import cl.venegas.buses_api.domain.model.Booking;
import cl.venegas.buses_api.domain.model.BookingStatus;
import cl.venegas.buses_api.domain.model.SeatHold;
import cl.venegas.buses_api.domain.model.valueobject.SeatNumber;
import cl.venegas.buses_api.domain.port.BookingRepository;
import cl.venegas.buses_api.domain.port.SeatHoldRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
<<<<<<< HEAD
        Long userId = 10L;
        Long tripId = 100L;
        List<SeatNumber> seats = List.of(new SeatNumber("A1"), new SeatNumber("A2"));
=======
        Long userId = 100L;
        Long tripId = 50L;
        List<String> seats = List.of("A1", "A2");
>>>>>>> parent of 85b11cc (Suma de test)

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setUserId(userId);
        booking.setTripId(tripId);
        booking.setSeats(seats);
        booking.setStatus(BookingStatus.PENDIENTE);

        List<SeatHold> seatHolds = List.of(
                new SeatHold(1L, tripId, "A1", userId, LocalDateTime.now()),
                new SeatHold(2L, tripId, "A2", userId, LocalDateTime.now()));

<<<<<<< HEAD
        List<SeatHold> seatHolds = List.of(new SeatHold(1L, tripId, new SeatNumber("A1"), userId, LocalDateTime.now()),
                new SeatHold(2L, tripId, new SeatNumber("A2"), userId, LocalDateTime.now()));
=======
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
>>>>>>> parent of 85b11cc (Suma de test)
        when(seatHoldRepository.findByTripIdAndSeatNumberIn(tripId, seats)).thenReturn(seatHolds);

        // 2. ACT
        cancelBookingService.handle(bookingId, userId);

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
        Long userId = 100L;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        // 2. ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> cancelBookingService.handle(bookingId, userId));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el usuario no es el dueño de la reserva")
    void shouldThrowExceptionWhenUserIsNotOwner() {
        // 1. ARRANGE
        Long bookingId = 1L;
        Long userId = 100L;
        Long otherUserId = 200L;

        Booking booking = new Booking();
        booking.setUserId(otherUserId); // Dueño diferente

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // 2. ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> cancelBookingService.handle(bookingId, userId));
    }

    @Test
    @DisplayName("Debe lanzar excepción si la reserva ya está cancelada")
    void shouldThrowExceptionWhenBookingAlreadyCancelled() {
        // 1. ARRANGE
        Long bookingId = 1L;
        Long userId = 100L;

        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setStatus(BookingStatus.CANCELADO);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // 2. ACT & ASSERT
        assertThrows(IllegalStateException.class, () -> cancelBookingService.handle(bookingId, userId));
    }

    @Test
    @DisplayName("Debe lanzar excepción si la reserva ha expirado")
    void shouldThrowExceptionWhenBookingExpired() {
        // 1. ARRANGE
        Long bookingId = 1L;
        Long userId = 100L;

        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setStatus(BookingStatus.EXPIRADO);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // 2. ACT & ASSERT
        assertThrows(IllegalStateException.class, () -> cancelBookingService.handle(bookingId, userId));
    }
}
