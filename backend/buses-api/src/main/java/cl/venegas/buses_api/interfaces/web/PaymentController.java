package cl.venegas.buses_api.interfaces.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

import cl.venegas.buses_api.application.dto.CreatePaymentIntentRequest;
import cl.venegas.buses_api.application.dto.CreatePaymentIntentResponse;
import cl.venegas.buses_api.application.usecase.payment.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-payment-intent")
    public ResponseEntity<?> createPaymentIntent(@RequestBody CreatePaymentIntentRequest request) {
        try {
            log.info("Creando preferencia de pago para monto: {}", request.amount());
            CreatePaymentIntentResponse response = paymentService.createPaymentIntent(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al crear preferencia de pago: {}", e.getMessage());
            // Mensaje generico al cliente: no exponer detalles internos
            return ResponseEntity.status(500).body(Map.of("error", "No se pudo iniciar el pago. Intenta nuevamente."));
        }
    }

    @PostMapping("/process-payment")
    public ResponseEntity<?> processPayment(@RequestBody Map<String, Object> paymentData) {
        try {
            log.info("Procesando pago via MercadoPago");
            Map<String, Object> result = paymentService.processPayment(paymentData);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error al procesar pago: {}", e.getMessage());
            Map<String, String> errorResponse = new java.util.HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("status_detail", "Error al procesar el pago.");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(@RequestBody Map<String, Object> payload) {
        try {
            java.util.Map<String, String> result = paymentService.confirmPaymentAndSendEmail(payload);
            return ResponseEntity.ok(result != null ? result : Map.of("idReserva", ""));
        } catch (IllegalArgumentException e) {
            log.warn("Intento de pago invalido: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Solicitud de pago invalida."));
        } catch (Exception e) {
            log.error("Error al confirmar pago: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", "Error al confirmar el pago."));
        }
    }
}
