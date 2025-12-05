package cl.venegas.buses_api.application.usecase.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
        // 1. Find booking and validate
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada: " + bookingId));

        if (booking.getStatus() != BookingStatus.PENDIENTE) {
            throw new IllegalStateException("La reserva no está en estado pendiente: " + booking.getStatus());
        }

        // Check if already paid
        if (paymentRepository.findByBookingId(bookingId).isPresent()) {
            throw new IllegalStateException("La reserva ya tiene un pago asociado");
        }

        BigDecimal amount = booking.getTotalAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Monto de pago inválido");
        }

        // 2. Process payment through gateway
        PaymentResult result = paymentGateway.processPayment(bookingId, amount, method);

        // 3. Create payment record
        PaymentStatus status = result.success() ? PaymentStatus.APROBADO : PaymentStatus.RECHAZADO;

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
        }

        return savedPayment;
    }
}
