package cl.venegas.buses_api.application.usecase.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.venegas.buses_api.domain.model.entity.Booking;
import cl.venegas.buses_api.domain.model.entity.Payment;
import cl.venegas.buses_api.domain.model.valueobject.BookingStatus;
import cl.venegas.buses_api.domain.model.valueobject.PaymentMethod;
import cl.venegas.buses_api.domain.model.valueobject.PaymentStatus;
import cl.venegas.buses_api.domain.repository.BookingRepository;
import cl.venegas.buses_api.domain.repository.PaymentRepository;
import cl.venegas.buses_api.domain.service.PaymentGateway;
import cl.venegas.buses_api.domain.service.PaymentGateway.PaymentResult;

@Service
@Slf4j
public class ProcessPaymentService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGateway paymentGateway;

    public ProcessPaymentService(
            BookingRepository bookingRepository,
            PaymentRepository paymentRepository,
            PaymentGateway paymentGateway) {
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.paymentGateway = paymentGateway;
    }

    @Transactional
    public Payment execute(Long bookingId, PaymentMethod method) {
        log.info("Iniciando procesamiento de pago: bookingId={}, method={}", bookingId, method);

        // 1. Find booking and validate
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.error("Reserva no encontrada para pago: bookingId={}", bookingId);
                    return new IllegalArgumentException("Reserva no encontrada: " + bookingId);
                });

        if (booking.getStatus() != BookingStatus.PENDIENTE) {
            log.warn("Intento de pago en reserva no pendiente: bookingId={}, status={}", bookingId,
                    booking.getStatus());
            throw new IllegalStateException("La reserva no está en estado pendiente: " + booking.getStatus());
        }

        // Check if already paid
        if (paymentRepository.findByBookingId(bookingId).isPresent()) {
            log.warn("Intento de pago duplicado: bookingId={}", bookingId);
            throw new IllegalStateException("La reserva ya tiene un pago asociado");
        }

        BigDecimal amount = booking.getTotalAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Monto inválido para pago: bookingId={}, amount={}", bookingId, amount);
            throw new IllegalArgumentException("Monto de pago inválido");
        }

        // 2. Process payment through gateway
        log.debug("Procesando pago con gateway: bookingId={}, amount={}", bookingId, amount);
        PaymentResult result = paymentGateway.processPayment(bookingId, amount, method);

        // 3. Create payment record
        PaymentStatus status = result.success() ? PaymentStatus.APROBADO : PaymentStatus.RECHAZADO;
        log.info("Resultado de pago: bookingId={}, status={}, txId={}", bookingId, status, result.transactionId());

        Payment payment = new Payment(
                null,
                bookingId,
                amount,
                method,
                status,
                result.transactionId(),
                result.gatewayResponse(),
                LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);

        // 4. Update booking status if payment succeeded
        if (result.success()) {
            booking.setStatus(BookingStatus.CONFIRMADO);
            booking.setPaymentReference(result.transactionId());
            bookingRepository.save(booking);
            log.info("Reserva confirmada tras pago exitoso: bookingId={}", bookingId);
        } else {
            log.warn("Pago fallido: bookingId={}, response={}", bookingId, result.gatewayResponse());
        }

        return savedPayment;
    }
}
