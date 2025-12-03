package cl.venegas.buses_api.application.usecase.booking;

import cl.venegas.buses_api.domain.model.entity.Booking;
import cl.venegas.buses_api.domain.model.valueobject.BookingStatus;
import cl.venegas.buses_api.domain.repository.BookingRepository;
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
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para ConfirmBookingService
 * Adaptado de la versión PRE-migración (Hexagonal) a Onion Architecture
 * 
 * Cambios respecto a la versión anterior:
 * - Paquete cambió de application.usecase a application.usecase.booking
 * - Método cambió de handle(Long bookingId, String paymentRef) a execute(String
 * bookingId)
 * - Ya no recibe paymentReference como parámetro
 * - Repositorios en domain.repository en vez de domain.port
 * - BookingStatus en domain.model.valueobject
 * - Booking en domain.model.entity
 */
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
        String bookingIdStr = "1";

        Booking booking = new Booking();
        booking.setStatus(BookingStatus.PENDIENTE);
        booking.setExpiresAt(LocalDateTime.now().plusMinutes(10));

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArguments()[0]);

        // 2. ACT
        Booking result = confirmBookingService.execute(bookingIdStr);

        // 3. ASSERT
        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(bookingCaptor.capture());
        Booking savedBooking = bookingCaptor.getValue();

        assertEquals(BookingStatus.CONFIRMADO, savedBooking.getStatus());
        assertNotNull(result);
    }

    @Test
    @DisplayName("Debe expirar la reserva si el tiempo ha pasado")
    void shouldExpireBookingIfTimePassed() {
        // 1. ARRANGE
        Long bookingId = 1L;
        String bookingIdStr = "1";

        Booking booking = new Booking();
        booking.setStatus(BookingStatus.PENDIENTE);
        booking.setExpiresAt(LocalDateTime.now().minusMinutes(1)); // Ya expirado

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArguments()[0]);

        // 2. ACT & ASSERT
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> confirmBookingService.execute(bookingIdStr));

        assertTrue(exception.getMessage().toLowerCase().contains("expir"));
        assertEquals(BookingStatus.EXPIRADO, booking.getStatus());

        // Verificar que se guardó el estado EXPIRADO
        verify(bookingRepository).save(booking);
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
                () -> confirmBookingService.execute(bookingIdStr));
    }

    @Test
    @DisplayName("Debe lanzar excepción si la reserva no está pendiente")
    void shouldThrowExceptionWhenBookingIsNotPending() {
        // 1. ARRANGE
        Long bookingId = 1L;
        String bookingIdStr = "1";

        Booking booking = new Booking();
        booking.setStatus(BookingStatus.CONFIRMADO); // Ya confirmada

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // 2. ACT & ASSERT
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> confirmBookingService.execute(bookingIdStr));

        assertTrue(exception.getMessage().toLowerCase().contains("pendiente")
                || exception.getMessage().contains(booking.getStatus().toString()));
    }

    @Test
    @DisplayName("Debe lanzar excepción si la reserva está cancelada")
    void shouldThrowExceptionWhenBookingIsCancelled() {
        // 1. ARRANGE
        Long bookingId = 1L;
        String bookingIdStr = "1";

        Booking booking = new Booking();
        booking.setStatus(BookingStatus.CANCELADO);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // 2. ACT & ASSERT
        assertThrows(IllegalStateException.class,
                () -> confirmBookingService.execute(bookingIdStr));
    }

    @Test
    @DisplayName("Debe lanzar excepción si la reserva ya expiró previamente")
    void shouldThrowExceptionWhenBookingAlreadyExpired() {
        // 1. ARRANGE
        Long bookingId = 1L;
        String bookingIdStr = "1";

        Booking booking = new Booking();
        booking.setStatus(BookingStatus.EXPIRADO);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // 2. ACT & ASSERT
        assertThrows(IllegalStateException.class,
                () -> confirmBookingService.execute(bookingIdStr));
    }

    @Test
    @DisplayName("Debe parsear correctamente el bookingId de String a Long")
    void shouldParseBookingIdCorrectly() {
        // 1. ARRANGE
        String bookingIdStr = "12345";
        Long expectedId = 12345L;

        Booking booking = new Booking();
        booking.setStatus(BookingStatus.PENDIENTE);
        booking.setExpiresAt(LocalDateTime.now().plusMinutes(10));

        when(bookingRepository.findById(expectedId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArguments()[0]);

        // 2. ACT
        confirmBookingService.execute(bookingIdStr);

        // 3. ASSERT
        verify(bookingRepository).findById(expectedId);
    }

    @Test
    @DisplayName("Debe confirmar reserva sin expiración definida")
    void shouldConfirmBookingWithoutExpiration() {
        // 1. ARRANGE
        Long bookingId = 1L;
        String bookingIdStr = "1";

        Booking booking = new Booking();
        booking.setStatus(BookingStatus.PENDIENTE);
        booking.setExpiresAt(null); // Sin expiración definida

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArguments()[0]);

        // 2. ACT
        Booking result = confirmBookingService.execute(bookingIdStr);

        // 3. ASSERT
        assertEquals(BookingStatus.CONFIRMADO, result.getStatus());
    }
}
