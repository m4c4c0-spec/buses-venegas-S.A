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
    @DisplayName("Debe cancelar la reserva correctamente y liberar asientos")
    void shouldCancelBookingSuccessfully() {
        // ARRANGE
        Long bookingId = 1L;
        Long userId = 10L;
        Long tripId = 100L;
        List<String> seats = List.of("A1", "A2");

        Booking booking = new Booking(bookingId, userId, tripId,
                seats.stream().map(SeatNumber::new).toList(),
                null, BookingStatus.PENDIENTE, null, null, null, null);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        List<SeatHold> seatHolds = List.of(new SeatHold(1L, tripId, "A1", userId, null),
                new SeatHold(2L, tripId, "A2", userId, null));

        // The service converts SeatNumber to String before calling the repository
        when(seatHoldRepository.findByTripIdAndSeatNumberIn(tripId, seats)).thenReturn(seatHolds);

        // ACT
        cancelBookingService.handle(bookingId, userId);

        // ASSERT
        assertEquals(BookingStatus.CANCELADO, booking.getStatus());
        verify(bookingRepository).save(booking);
        verify(seatHoldRepository).deleteAll(seatHolds);
    }

    @Test
    @DisplayName("Debe lanzar excepción si la reserva no existe")
    void shouldThrowExceptionWhenBookingNotFound() {
        Long bookingId = 1L;
        Long userId = 10L;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> cancelBookingService.handle(bookingId, userId));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el usuario no corresponde a la reserva")
    void shouldThrowExceptionWhenUserDoesNotMatch() {
        Long bookingId = 1L;
        Long userId = 10L;
        Long otherUserId = 99L;

        Booking booking = new Booking(bookingId, otherUserId, 100L, List.of(), null, BookingStatus.PENDIENTE, null,
                null, null, null);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(IllegalArgumentException.class, () -> cancelBookingService.handle(bookingId, userId));
    }

    @Test
    @DisplayName("Debe lanzar excepción si la reserva ya está cancelada")
    void shouldThrowExceptionWhenBookingAlreadyCancelled() {
        Long bookingId = 1L;
        Long userId = 10L;

        Booking booking = new Booking(bookingId, userId, 100L, List.of(), null, BookingStatus.CANCELADO, null, null,
                null, null);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(IllegalStateException.class, () -> cancelBookingService.handle(bookingId, userId));
    }

    @Test
    @DisplayName("Debe lanzar excepción si la reserva ha expirado")
    void shouldThrowExceptionWhenBookingExpired() {
        Long bookingId = 1L;
        Long userId = 10L;

        Booking booking = new Booking(bookingId, userId, 100L, List.of(), null, BookingStatus.EXPIRADO, null, null,
                null, null);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(IllegalStateException.class, () -> cancelBookingService.handle(bookingId, userId));
    }
}
