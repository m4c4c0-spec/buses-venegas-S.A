package cl.venegas.buses_api.application.usecase;

import cl.venegas.buses_api.domain.model.SeatHold;
import cl.venegas.buses_api.domain.port.SeatHoldRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
        String seatNumber = "A1";

        SeatHold expectedHold = new SeatHold(1L, tripId, seatNumber, userId, LocalDateTime.now());

        when(seatHoldRepository.hold(eq(tripId), eq(seatNumber), eq(userId), any(LocalDateTime.class)))
                .thenReturn(expectedHold);
        // 2. ACT
        SeatHold result = holdSeatsService.handle(tripId, seatNumber, userId);
        // 3. ASSERT
        assertNotNull(result);
        assertEquals("A1", result.seat());
        verify(seatHoldRepository).hold(eq(tripId), eq(seatNumber), eq(userId), any(LocalDateTime.class));
    }
}
