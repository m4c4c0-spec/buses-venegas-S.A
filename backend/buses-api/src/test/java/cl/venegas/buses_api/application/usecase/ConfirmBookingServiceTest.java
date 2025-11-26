package cl.venegas.buses_api.application.usecase;

import cl.venegas.buses_api.domain.model.Booking;
import cl.venegas.buses_api.domain.model.BookingStatus;
import cl.venegas.buses_api.domain.port.BookingRepository;
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
class ConfirmBookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private ConfirmBookingService confirmBookingService;

    @Test
    @DisplayName("Debe confirmar la reserva si está pendiente y no expirada")
    void shouldConfirmBookingSuccessfully() {
        // 1. ARRANGE
        Long bookingId = 1L;
        String paymentRef = "PAY-123";
        Booking booking = new Booking(bookingId, 1L, 1L, List.of(), List.of(), BookingStatus.PENDIENTE, null, null,
                LocalDateTime.now(), LocalDateTime.now().plusMinutes(10));

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // 2. ACT
        confirmBookingService.handle(bookingId, paymentRef);

        // 3. ASSERT
        org.mockito.ArgumentCaptor<Booking> bookingCaptor = org.mockito.ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(bookingCaptor.capture());
        Booking savedBooking = bookingCaptor.getValue();

        assertEquals(BookingStatus.CONFIRMADO, savedBooking.getStatus());
        assertEquals(paymentRef, savedBooking.getPaymentReference());
    }

    @Test
    @DisplayName("Debe expirar la reserva si el tiempo ha pasado")
    void shouldExpireBookingIfTimePassed() {
        // 1. ARRANGE
        Long bookingId = 1L;
        Booking booking = new Booking(bookingId, 1L, 1L, List.of(), List.of(), BookingStatus.PENDIENTE, null, null,
                LocalDateTime.now(), LocalDateTime.now().minusMinutes(1));

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        // 2. ACT & ASSERT
        assertThrows(IllegalStateException.class, () -> confirmBookingService.handle(bookingId, "PAY-123"),
                "La reserva ha expirado");
        assertEquals(BookingStatus.EXPIRADO, booking.getStatus());
    }

    @Test
    @DisplayName("Debe lanzar excepción si la reserva no existe")
    void shouldThrowExceptionWhenBookingNotFound() {
        // 1. ARRANGE
        Long bookingId = 999L;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());
        // 2. ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> confirmBookingService.handle(bookingId, "PAY-123"),
                "Reserva no encontrada por id: " + bookingId);
    }

    @Test
    @DisplayName("Debe lanzar excepción si la reserva no está pendiente")
    void shouldThrowExceptionWhenBookingIsNotPending() {
        // 1. ARRANGE
        Long bookingId = 1L;
        Booking booking = new Booking(bookingId, 1L, 1L, List.of(), List.of(), BookingStatus.CONFIRMADO, null, null,
                LocalDateTime.now(), LocalDateTime.now().plusMinutes(10));

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        // 2. ACT & ASSERT
        assertThrows(IllegalStateException.class, () -> confirmBookingService.handle(bookingId, "PAY-123"),
                "La reserva no está en estado PENDIENTE. Estado actual: " + booking.getStatus());
    }
}
