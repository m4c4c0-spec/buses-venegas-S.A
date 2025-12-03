package cl.venegas.buses_api.application.usecase.seat;

import cl.venegas.buses_api.application.Command.HoldSeatsCommand;
import cl.venegas.buses_api.domain.model.entity.SeatHold;
import cl.venegas.buses_api.domain.repository.SeatHoldRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para HoldSeatsService
 * Adaptado de la versión PRE-migración (Hexagonal) a Onion Architecture
 * 
 * Cambios respecto a la versión anterior:
 * - Paquete cambió de application.usecase a application.usecase.seat
 * - Método cambió de handle(Long tripId, String seat, Long userId) a
 * execute(HoldSeatsCommand)
 * - Ahora usa el patrón Command (HoldSeatsCommand)
 * - Devuelve List<SeatHold> en vez de un solo SeatHold
 * - Repositorio en domain.repository en vez de domain.port
 * - SeatHold en domain.model.entity
 * - seat es String directamente (ya no usa SeatNumber Value Object)
 */
@ExtendWith(MockitoExtension.class)
class HoldSeatsServiceTest {

    @Mock
    private SeatHoldRepository seatHoldRepository;

    @InjectMocks
    private HoldSeatsService holdSeatsService;

    @Test
    @DisplayName("Debe bloquear un asiento correctamente y asignar expiración")
    void shouldHoldSeatSuccessfully() {
        // 1. ARRANGE
        Long tripId = 100L;
        Long userId = 1L;
        List<String> seats = List.of("A1");

        HoldSeatsCommand command = new HoldSeatsCommand(tripId, seats, userId);

        SeatHold expectedHold = new SeatHold(1L, tripId, "A1", userId, LocalDateTime.now().plusMinutes(10));

        when(seatHoldRepository.hold(eq(tripId), eq("A1"), eq(userId), any(LocalDateTime.class)))
                .thenReturn(expectedHold);

        // 2. ACT
        List<SeatHold> result = holdSeatsService.execute(command);

        // 3. ASSERT
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("A1", result.get(0).seat());
        assertEquals(tripId, result.get(0).tripId());
        assertEquals(userId, result.get(0).userId());

        verify(seatHoldRepository).hold(eq(tripId), eq("A1"), eq(userId), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Debe bloquear múltiples asientos correctamente")
    void shouldHoldMultipleSeatsSuccessfully() {
        // 1. ARRANGE
        Long tripId = 100L;
        Long userId = 1L;
        List<String> seats = List.of("A1", "A2", "A3");

        HoldSeatsCommand command = new HoldSeatsCommand(tripId, seats, userId);

        when(seatHoldRepository.hold(eq(tripId), eq("A1"), eq(userId), any(LocalDateTime.class)))
                .thenReturn(new SeatHold(1L, tripId, "A1", userId, LocalDateTime.now().plusMinutes(10)));
        when(seatHoldRepository.hold(eq(tripId), eq("A2"), eq(userId), any(LocalDateTime.class)))
                .thenReturn(new SeatHold(2L, tripId, "A2", userId, LocalDateTime.now().plusMinutes(10)));
        when(seatHoldRepository.hold(eq(tripId), eq("A3"), eq(userId), any(LocalDateTime.class)))
                .thenReturn(new SeatHold(3L, tripId, "A3", userId, LocalDateTime.now().plusMinutes(10)));

        // 2. ACT
        List<SeatHold> result = holdSeatsService.execute(command);

        // 3. ASSERT
        assertNotNull(result);
        assertEquals(3, result.size());

        // Verificar que se llamó hold() para cada asiento
        verify(seatHoldRepository).hold(eq(tripId), eq("A1"), eq(userId), any(LocalDateTime.class));
        verify(seatHoldRepository).hold(eq(tripId), eq("A2"), eq(userId), any(LocalDateTime.class));
        verify(seatHoldRepository).hold(eq(tripId), eq("A3"), eq(userId), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Debe asignar correctamente la expiración a 10 minutos")
    void shouldAssignCorrectExpirationTime() {
        // 1. ARRANGE
        Long tripId = 100L;
        Long userId = 1L;
        List<String> seats = List.of("B5");

        HoldSeatsCommand command = new HoldSeatsCommand(tripId, seats, userId);

        LocalDateTime beforeExecution = LocalDateTime.now();

        when(seatHoldRepository.hold(eq(tripId), eq("B5"), eq(userId), any(LocalDateTime.class)))
                .thenAnswer(invocation -> {
                    LocalDateTime expiresAt = invocation.getArgument(3);
                    return new SeatHold(1L, tripId, "B5", userId, expiresAt);
                });

        // 2. ACT
        List<SeatHold> result = holdSeatsService.execute(command);

        // 3. ASSERT
        assertNotNull(result);
        assertFalse(result.isEmpty());

        LocalDateTime expiresAt = result.get(0).expiresAt();
        assertNotNull(expiresAt);

        // Verificar que expira en aproximadamente 10 minutos
        assertTrue(expiresAt.isAfter(beforeExecution.plusMinutes(9)));
        assertTrue(expiresAt.isBefore(beforeExecution.plusMinutes(11)));
    }

    @Test
    @DisplayName("Debe retornar lista vacía si no hay asientos para bloquear")
    void shouldReturnEmptyListWhenNoSeats() {
        // 1. ARRANGE
        Long tripId = 100L;
        Long userId = 1L;
        List<String> seats = List.of();

        HoldSeatsCommand command = new HoldSeatsCommand(tripId, seats, userId);

        // 2. ACT
        List<SeatHold> result = holdSeatsService.execute(command);

        // 3. ASSERT
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verificar que no se llamó al repositorio
        verify(seatHoldRepository, never()).hold(anyLong(), anyString(), anyLong(), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Debe preservar el orden de los asientos bloqueados")
    void shouldPreserveOrderOfHeldSeats() {
        // 1. ARRANGE
        Long tripId = 100L;
        Long userId = 1L;
        List<String> seats = List.of("C1", "A5", "B3");

        HoldSeatsCommand command = new HoldSeatsCommand(tripId, seats, userId);

        when(seatHoldRepository.hold(eq(tripId), eq("C1"), eq(userId), any(LocalDateTime.class)))
                .thenReturn(new SeatHold(1L, tripId, "C1", userId, LocalDateTime.now().plusMinutes(10)));
        when(seatHoldRepository.hold(eq(tripId), eq("A5"), eq(userId), any(LocalDateTime.class)))
                .thenReturn(new SeatHold(2L, tripId, "A5", userId, LocalDateTime.now().plusMinutes(10)));
        when(seatHoldRepository.hold(eq(tripId), eq("B3"), eq(userId), any(LocalDateTime.class)))
                .thenReturn(new SeatHold(3L, tripId, "B3", userId, LocalDateTime.now().plusMinutes(10)));

        // 2. ACT
        List<SeatHold> result = holdSeatsService.execute(command);

        // 3. ASSERT
        assertEquals(3, result.size());
        assertEquals("C1", result.get(0).seat());
        assertEquals("A5", result.get(1).seat());
        assertEquals("B3", result.get(2).seat());
    }

    @Test
    @DisplayName("Debe asociar correctamente el tripId y userId a cada hold")
    void shouldAssociateCorrectTripAndUserToEachHold() {
        // 1. ARRANGE
        Long tripId = 42L;
        Long userId = 99L;
        List<String> seats = List.of("D7", "D8");

        HoldSeatsCommand command = new HoldSeatsCommand(tripId, seats, userId);

        when(seatHoldRepository.hold(eq(tripId), anyString(), eq(userId), any(LocalDateTime.class)))
                .thenAnswer(invocation -> {
                    String seat = invocation.getArgument(1);
                    return new SeatHold(1L, tripId, seat, userId, LocalDateTime.now().plusMinutes(10));
                });

        // 2. ACT
        List<SeatHold> result = holdSeatsService.execute(command);

        // 3. ASSERT
        assertEquals(2, result.size());

        for (SeatHold hold : result) {
            assertEquals(tripId, hold.tripId());
            assertEquals(userId, hold.userId());
        }
    }
}
