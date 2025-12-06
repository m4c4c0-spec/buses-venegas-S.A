package cl.venegas.buses_api.domain.service;

import java.math.BigDecimal;

import cl.venegas.buses_api.domain.model.valueobject.PaymentMethod;

/**
 * Port for payment gateway operations.
 * This interface defines the contract for processing payments,
 * following hexagonal architecture principles.
 */
public interface PaymentGateway {

    /**
     * Process a payment for a booking.
     * 
     * @param bookingId the booking ID
     * @param amount    the amount to charge
     * @param method    the payment method
     * @return the result of the payment processing
     */
    PaymentResult processPayment(Long bookingId, BigDecimal amount, PaymentMethod method);

    /**
     * Result of a payment operation.
     */
    record PaymentResult(
            boolean success,
            String transactionId,
            String gatewayResponse,
            String errorMessage) {
        public static PaymentResult success(String transactionId, String gatewayResponse) {
            return new PaymentResult(true, transactionId, gatewayResponse, null);
        }

        public static PaymentResult failure(String errorMessage) {
            return new PaymentResult(false, null, null, errorMessage);
        }
    }
}
