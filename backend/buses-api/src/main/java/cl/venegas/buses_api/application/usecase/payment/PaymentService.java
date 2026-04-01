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
    public java.util.Map<String, String> confirmPaymentAndSendEmail(Map<String, Object> payload) {
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

                // ⚡ WORKAROUND: Disparo Asíncrono para que el Frontend regrese el OK en 0ms y no se congele la UI
                // por culpa de la lentitud de SMTP/Gmail o si PostgreSQL Free-Tier está invernando.
                java.util.concurrent.CompletableFuture.runAsync(() -> {
                    try {
                        reservaRepository.save(reserva);
                        
                        // 🔗 REGISTRO BLOCKCHAIN (SEPOLIA)
                        if(hexHash != null) {
                            String txHash = blockchainService.registerTicketOnChain(idReserva, hexHash);
                            System.out.println("🔗 Hash Registrado en Blockchain: " + hexHash);
                            detalles.put("ticketHash", hexHash); // Agregamos el hash a los detalles para el correo
                        }

                        emailService.sendReceiptEmail(detalles);
                        System.out.println("✅ OK: Reserva e email procesados asíncronamente: " + idReserva);
                    } catch (Exception e) {
                        System.err.println("❌ ERROR asíncrono en save, email o blockchain: " + e.getMessage());
                        e.printStackTrace();
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
