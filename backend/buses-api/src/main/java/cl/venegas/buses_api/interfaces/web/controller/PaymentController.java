package cl.venegas.buses_api.interfaces.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import cl.venegas.buses_api.application.usecase.payment.ProcessPaymentService;
import cl.venegas.buses_api.domain.model.entity.Payment;
import cl.venegas.buses_api.domain.repository.PaymentRepository;
import cl.venegas.buses_api.interfaces.web.dto.request.ProcessPaymentRequest;
import cl.venegas.buses_api.interfaces.web.dto.response.PaymentResponse;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final ProcessPaymentService processPaymentService;
    private final PaymentRepository paymentRepository;

    public PaymentController(
            ProcessPaymentService processPaymentService,
            PaymentRepository paymentRepository) {
        this.processPaymentService = processPaymentService;
        this.paymentRepository = paymentRepository;
    }

    /**
     * POST /api/v1/payments
     * Process a payment for a booking
     */
    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(
            @RequestBody @Validated ProcessPaymentRequest request) {

        Payment payment = processPaymentService.execute(request.bookingId(), request.method());

        PaymentResponse response = toResponse(payment);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/v1/payments/{id}
     * Get payment by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long id) {
        return paymentRepository.findById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/v1/payments/booking/{bookingId}
     * Get payment by booking ID
     */
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<PaymentResponse> getPaymentByBooking(@PathVariable Long bookingId) {
        return paymentRepository.findByBookingId(bookingId)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.id(),
                payment.bookingId(),
                payment.amount(),
                payment.method(),
                payment.status(),
                payment.transactionId(),
                payment.createdAt());
    }
}
