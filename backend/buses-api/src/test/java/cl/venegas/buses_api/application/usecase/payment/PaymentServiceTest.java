package cl.venegas.buses_api.application.usecase.payment;

import cl.venegas.buses_api.application.usecase.email.EmailService;
import cl.venegas.buses_api.domain.model.entity.Reserva;
import cl.venegas.buses_api.domain.model.entity.ReservaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private EmailService emailService;

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(paymentService, "mpAccessToken", "TEST_TOKEN");
    }

    @Test
    void confirmPaymentAndSendEmail_shouldProcessCorrectly() throws Exception {
        // Arrange
        Map<String, Object> payload = new HashMap<>();
        Map<String, Object> detalles = new HashMap<>();

        detalles.put("origen", "Santiago");
        detalles.put("destino", "Concepcion");
        detalles.put("fechaIda", "2024-12-10");
        detalles.put("idaYVuelta", true);
        detalles.put("fechaVuelta", "2024-12-15");
        detalles.put("precioTotal", 15000); 
        
        Map<String, Object> horarioViaje = new HashMap<>();
        horarioViaje.put("salida", "08:00");
        horarioViaje.put("llegada", "14:00");
        detalles.put("horarioViaje", horarioViaje);

        List<Map<String, Object>> pasajerosData = new ArrayList<>();
        Map<String, Object> p1 = new HashMap<>();
        p1.put("email", "test@test.com");
        p1.put("nombre", "Juan");
        pasajerosData.add(p1);
        detalles.put("pasajerosData", pasajerosData);

        payload.put("detalles", detalles);

        when(objectMapper.writeValueAsString(any())).thenReturn("[{\"email\":\"test@test.com\",\"nombre\":\"Juan\"}]");
        
        // Act
        String result = paymentService.confirmPaymentAndSendEmail(payload);

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("BB"));

        ArgumentCaptor<Reserva> reservaCaptor = ArgumentCaptor.forClass(Reserva.class);
        verify(reservaRepository, times(1)).save(reservaCaptor.capture());
        
        Reserva savedReserva = reservaCaptor.getValue();
        assertEquals("Santiago", savedReserva.getOrigen());
        assertEquals("Concepcion", savedReserva.getDestino());
        assertEquals("2024-12-10", savedReserva.getFechaViaje());
        assertEquals(15000, savedReserva.getPrecioTotal());
        assertEquals("08:00", savedReserva.getHorarioSalida());
        assertTrue(savedReserva.getIdaYVuelta());

        verify(emailService, times(1)).sendReceiptEmail(detalles);
        assertNotNull(payload.get("idReserva"));
    }

    @Test
    void confirmPaymentAndSendEmail_shouldHandleStringPriceRobustly() throws Exception {
        // Arrange
        Map<String, Object> payload = new HashMap<>();
        Map<String, Object> detalles = new HashMap<>();

        detalles.put("origen", "Santiago");
        detalles.put("destino", "Valparaiso");
        detalles.put("precioTotal", "15.000"); // Emulando formato chileno de precio
        
        payload.put("detalles", detalles);

        // Act
        String result = paymentService.confirmPaymentAndSendEmail(payload);

        // Assert
        assertNotNull(result);
        ArgumentCaptor<Reserva> reservaCaptor = ArgumentCaptor.forClass(Reserva.class);
        verify(reservaRepository).save(reservaCaptor.capture());
        
        Reserva savedReserva = reservaCaptor.getValue();
        assertEquals(15000, savedReserva.getPrecioTotal());
    }
}
