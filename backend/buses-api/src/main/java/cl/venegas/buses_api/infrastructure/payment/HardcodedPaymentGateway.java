package cl.venegas.buses_api.infrastructure.payment;

import java.math.BigDecimal;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cl.venegas.buses_api.domain.model.valueobject.PaymentMethod;
import cl.venegas.buses_api.domain.service.PaymentGateway;

/**
 * Hardcoded payment gateway implementation for testing purposes.
 * This implementation always approves payments with a simulated delay.
 */
@Component
public class HardcodedPaymentGateway implements PaymentGateway {

    private static final Logger log = LoggerFactory.getLogger(HardcodedPaymentGateway.class);

    @Override
    public PaymentResult processPayment(Long bookingId, BigDecimal amount, PaymentMethod method) {
        log.info("Iniciando procesamiento de pago - BookingId: {}, Monto: ${}, Método: {}",
                bookingId, amount, method);

        // Simulate processing delay (500-1500ms)
        long delay = 500 + (long) (Math.random() * 1000);
        log.debug("Simulando delay de procesamiento: {}ms", delay);

        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Procesamiento de pago interrumpido para bookingId: {}", bookingId);
        }

        // Generate a fake transaction ID
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Build gateway response (simulated)
        String gatewayResponse = String.format(
                "{\"status\":\"APPROVED\",\"transactionId\":\"%s\",\"bookingId\":%d,\"amount\":%.2f,\"method\":\"%s\",\"timestamp\":\"%s\"}",
                transactionId,
                bookingId,
                amount,
                method.name(),
                java.time.LocalDateTime.now().toString());

        log.info("Pago procesado exitosamente - TransactionId: {}, BookingId: {}",
                transactionId, bookingId);
        log.debug("Gateway response: {}", gatewayResponse);

        return PaymentResult.success(transactionId, gatewayResponse);
    }
}
