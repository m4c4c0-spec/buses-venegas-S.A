package cl.venegas.buses_api.application.usecase.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import cl.venegas.buses_api.application.dto.CreatePaymentIntentRequest;
import cl.venegas.buses_api.application.dto.CreatePaymentIntentResponse;
import cl.venegas.buses_api.application.usecase.email.EmailService;

import java.util.Map;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import cl.venegas.buses_api.domain.model.entity.Reserva;
import cl.venegas.buses_api.domain.model.entity.ReservaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PaymentService {

    @Value("${mercadopago.access-token}")
    private String mpAccessToken;

    private final EmailService emailService;
    private final ReservaRepository reservaRepository;
    private final ObjectMapper objectMapper;

    public PaymentService(EmailService emailService, ReservaRepository reservaRepository, ObjectMapper objectMapper) {
        this.emailService = emailService;
        this.reservaRepository = reservaRepository;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        // SDK configuration removed as we shift to RestTemplate implementation
    }

    @SuppressWarnings("unchecked")
    public CreatePaymentIntentResponse createPaymentIntent(CreatePaymentIntentRequest request) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(mpAccessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String jsonBody = "{"
            + "\"items\": [{"
            + "\"title\": \"Pasaje de Buses Venegas\","
            + "\"quantity\": 1,"
            + "\"unit_price\": " + request.amount() + ","
            + "\"currency_id\": \"CLP\""
            + "}],"
            + "\"back_urls\": {"
            + "\"success\": \"http://localhost:5173\","
            + "\"failure\": \"http://localhost:5173\","
            + "\"pending\": \"http://localhost:5173\""
            + "}"
            + "}";

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity("https://api.mercadopago.com/checkout/preferences", entity, Map.class);
        
        String preferenceId = (String) response.getBody().get("id");
        return new CreatePaymentIntentResponse(preferenceId);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> processPayment(Map<String, Object> paymentData) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(mpAccessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Idempotency-Key", java.util.UUID.randomUUID().toString());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(paymentData, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity("https://api.mercadopago.com/v1/payments", entity, Map.class);
        
        return response.getBody();
    }

    @SuppressWarnings("unchecked")
    public String confirmPaymentAndSendEmail(Map<String, Object> payload) {
        try {
            Map<String, Object> detalles = (Map<String, Object>) payload.get("detalles");
            if (detalles != null) {
                // Generate Booking ID
                String idReserva = "BB" + (int)(Math.random() * 900000 + 100000);
                detalles.put("idReserva", idReserva); // Pass it to the email service

                String emailContacto = "";
                java.util.List<Map<String,Object>> pasajeros = (java.util.List<Map<String,Object>>) detalles.get("pasajerosData");
                if (pasajeros != null && !pasajeros.isEmpty() && pasajeros.get(0).get("email") != null) {
                    emailContacto = pasajeros.get(0).get("email").toString();
                }

                String pasajerosJson = objectMapper.writeValueAsString(pasajeros);

                Map<String, Object> horario = (Map<String, Object>) detalles.get("horarioViaje");
                String horarioSalida = horario != null ? (String) horario.get("salida") : "";
                String horarioLlegada = horario != null ? (String) horario.get("llegada") : "";

                Boolean idaYVuelta = (Boolean) detalles.get("idaYVuelta");
                String fechaVuelta = (String) detalles.get("fechaVuelta");

                Integer precioTotal = 0;
                if (detalles.get("precioTotal") != null) {
                    precioTotal = Integer.parseInt(detalles.get("precioTotal").toString());
                }

                Reserva reserva = Reserva.builder()
                        .id(idReserva)
                        .origen((String) detalles.get("origen"))
                        .destino((String) detalles.get("destino"))
                        .fechaViaje((String) detalles.get("fechaIda"))
                        .horarioSalida(horarioSalida)
                        .horarioLlegada(horarioLlegada)
                        .emailContacto(emailContacto)
                        .precioTotal(precioTotal)
                        .idaYVuelta(idaYVuelta != null ? idaYVuelta : false)
                        .fechaVuelta(fechaVuelta)
                        .pasajerosJson(pasajerosJson)
                        .build();

                reservaRepository.save(reserva);

                // Update payload response
                payload.put("idReserva", idReserva);
                emailService.sendReceiptEmail(detalles);
                return idReserva;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error processing email confirmation", e);
        }
    }
}
