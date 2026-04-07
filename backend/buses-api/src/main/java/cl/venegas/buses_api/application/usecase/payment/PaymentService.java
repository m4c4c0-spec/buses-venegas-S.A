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

import java.security.SecureRandom;
import java.util.Map;
import jakarta.annotation.PostConstruct;
import cl.venegas.buses_api.domain.model.entity.Reserva;
import cl.venegas.buses_api.domain.model.entity.ReservaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    // SecureRandom para IDs de reserva criptograficamente seguros
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Value("${mercadopago.access-token}")
    private String mpAccessToken;

    // URL del frontend para back_urls de MercadoPago (configurable por entorno)
    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    private final EmailService emailService;
    private final ReservaRepository reservaRepository;
    private final ObjectMapper objectMapper;
    private final BlockchainService blockchainService;

    public PaymentService(EmailService emailService, ReservaRepository reservaRepository, ObjectMapper objectMapper, BlockchainService blockchainService) {
        this.emailService = emailService;
        this.reservaRepository = reservaRepository;
        this.objectMapper = objectMapper;
        this.blockchainService = blockchainService;
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

        // back_urls ahora usa frontendUrl configurable (variable de entorno FRONTEND_URL)
        String jsonBody = "{"
            + "\"items\": [{"
            + "\"title\": \"Pasaje de Buses Venegas\","
            + "\"quantity\": 1,"
            + "\"unit_price\": " + request.amount() + ","
            + "\"currency_id\": \"CLP\""
            + "}],"
            + "\"back_urls\": {"
            + "\"success\": \"" + frontendUrl + "\","
            + "\"failure\": \"" + frontendUrl + "\","
            + "\"pending\": \"" + frontendUrl + "\""
            + "},"
            + "\"auto_return\": \"approved\""
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
    public java.util.Map<String, String> confirmPaymentAndSendEmail(Map<String, Object> payload) {
        try {
            // SEGURIDAD: Rechazar paymentIds de simulacion en produccion
            String paymentId = payload.get("paymentId") != null ? String.valueOf(payload.get("paymentId")) : "";
            if (paymentId.startsWith("SIMULACRO-")) {
                log.warn("Intento de pago simulado rechazado. PaymentId: {}", paymentId);
                throw new IllegalArgumentException("Pagos de simulacion no estan permitidos.");
            }

            Map<String, Object> detalles = (Map<String, Object>) payload.get("detalles");
            if (detalles != null) {
                // SEGURIDAD: Usar SecureRandom en lugar de Math.random() para IDs impredecibles
                String idReserva = "BB" + (100000 + SECURE_RANDOM.nextInt(900000));
                detalles.put("idReserva", idReserva);

                String emailContacto = "";
                java.util.List<Map<String,Object>> pasajeros = (java.util.List<Map<String,Object>>) detalles.get("pasajerosData");
                if (pasajeros != null && !pasajeros.isEmpty() && pasajeros.get(0).get("email") != null) {
                    emailContacto = pasajeros.get(0).get("email").toString();
                }

                String pasajerosJson = objectMapper.writeValueAsString(pasajeros);

                Map<String, Object> horario = (Map<String, Object>) detalles.get("horarioViaje");
                String horarioSalida = horario != null ? String.valueOf(horario.getOrDefault("salida", "")) : "";
                String horarioLlegada = horario != null ? String.valueOf(horario.getOrDefault("llegada", "")) : "";

                Boolean idaYVuelta = Boolean.parseBoolean(String.valueOf(detalles.get("idaYVuelta")));
                String fechaVuelta = detalles.get("fechaVuelta") != null ? String.valueOf(detalles.get("fechaVuelta")) : null;

                Integer precioTotal = 0;
                if (detalles.get("precioTotal") != null) {
                    try {
                        precioTotal = Integer.parseInt(String.valueOf(detalles.get("precioTotal")).replace(".",""));
                    } catch (Exception e) {
                        try {
                            precioTotal = (int) Double.parseDouble(String.valueOf(detalles.get("precioTotal")));
                        } catch(Exception ignored){}
                    }
                }

                String origenVal = String.valueOf(detalles.getOrDefault("origen", ""));
                String destinoVal = String.valueOf(detalles.getOrDefault("destino", ""));
                String fechaIdaVal = String.valueOf(detalles.getOrDefault("fechaIda", ""));

                final Reserva reserva = Reserva.builder()
                        .id(idReserva)
                        .origen(origenVal)
                        .destino(destinoVal)
                        .fechaViaje(fechaIdaVal)
                        .horarioSalida(horarioSalida)
                        .horarioLlegada(horarioLlegada)
                        .emailContacto(emailContacto)
                        .idaYVuelta(idaYVuelta)
                        .fechaVuelta(fechaVuelta)
                        .precioTotal(precioTotal)
                        .pasajerosJson(pasajerosJson)
                        .build();

                String hexHash = blockchainService.generateTicketHash(idReserva, origenVal, destinoVal, fechaIdaVal, pasajerosJson);

                java.util.concurrent.CompletableFuture.runAsync(() -> {
                    try {
                        reservaRepository.save(reserva);

                        if(hexHash != null) {
                            blockchainService.registerTicketOnChain(idReserva, hexHash);
                            log.info("Ticket registrado en blockchain. IdReserva: {}", idReserva);
                            detalles.put("ticketHash", hexHash);
                        }

                        emailService.sendReceiptEmail(detalles);
                        log.info("Reserva e email procesados correctamente. IdReserva: {}", idReserva);
                    } catch (Exception e) {
                        log.error("Error asincrono al procesar reserva o email. IdReserva: {}", e.getMessage(), e);
                    }
                });

                java.util.Map<String, String> result = new java.util.HashMap<>();
                result.put("idReserva", idReserva);
                if (hexHash != null) result.put("ticketHash", hexHash);
                return result;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error processing email confirmation", e);
        }
    }
}
