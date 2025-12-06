package cl.venegas.buses_api.interfaces.web.controller;

import cl.venegas.buses_api.application.Command.CreateBookingCommand;
import cl.venegas.buses_api.application.usecase.booking.CancelBookingService;
import cl.venegas.buses_api.application.usecase.booking.ConfirmBookingService;
import cl.venegas.buses_api.application.usecase.booking.CreateBookingUseCase;
import cl.venegas.buses_api.application.usecase.booking.GetUserBookingsService;
import cl.venegas.buses_api.domain.model.entity.Booking;
import cl.venegas.buses_api.interfaces.web.dto.request.CreateBookingRequest;
import cl.venegas.buses_api.interfaces.web.dto.response.BookingResponse;
import cl.venegas.buses_api.interfaces.web.mapper.BookingDTOMapper;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")

public class BookingsController {

    private static final Logger log = LoggerFactory.getLogger(BookingsController.class);

    private final CreateBookingUseCase createBookingUseCase;
    private final CancelBookingService cancelBookingService;
    private final ConfirmBookingService confirmBookingUseCase;
    private final GetUserBookingsService getUserBookingsUseCase;
    private final BookingDTOMapper mapper;

    public BookingsController(
            CreateBookingUseCase createBookingUseCase,
            CancelBookingService cancelBookingUseCase,
            ConfirmBookingService confirmBookingUseCase,
            GetUserBookingsService getUserBookingsUseCase,
            BookingDTOMapper mapper) {
        this.createBookingUseCase = createBookingUseCase;
        this.cancelBookingService = cancelBookingUseCase;
        this.confirmBookingUseCase = confirmBookingUseCase;
        this.getUserBookingsUseCase = getUserBookingsUseCase;
        this.mapper = mapper;
    }

    /**
     * POST /api/v1/bookings
     * Create un nuevo viaje
     */
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @RequestBody @Validated CreateBookingRequest request) {

        log.info("Recibido POST /api/v1/bookings: tripId={}, userId={}, seatsCount={}",
                request.tripId(), request.userId(), request.seats().size());

        try {
            CreateBookingCommand command = mapper.toCreateCommand(request);
            Booking booking = createBookingUseCase.execute(command);
            BookingResponse response = mapper.toResponse(booking);

            log.info("Reserva creada exitosamente: bookingId={}, totalAmount={}",
                    response.id(), response.totalAmount());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación al crear reserva: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al crear reserva", e);
            throw e;
        }
    }

    /**
     * GET /api/v1/bookings/user/{userId}
     * obtiene todos los viajes del usuario
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingResponse>> getUserBookings(
            @PathVariable String userId) {

        log.info("Recibido GET /api/v1/bookings/user/{}: userId={}", userId, userId);

        try {
            List<Booking> bookings = getUserBookingsUseCase.execute(userId);
            List<BookingResponse> response = bookings.stream()
                    .map(mapper::toResponse)
                    .collect(Collectors.toList());

            log.info("Retornando {} reservas para userId={}", response.size(), userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al obtener reservas de usuario: userId={}", userId, e);
            throw e;
        }
    }

    /**
     * PUT /api/v1/bookings/{bookingId}/confirm
     * Confirma el viaje
     */
    @PutMapping("/{bookingId}/confirm")
    public ResponseEntity<BookingResponse> confirmBooking(
            @PathVariable String bookingId) {

        log.info("Recibido PUT /api/v1/bookings/{}/confirm: bookingId={}", bookingId, bookingId);

        try {
            Booking booking = confirmBookingUseCase.execute(bookingId);
            BookingResponse response = mapper.toResponse(booking);

            log.info("Reserva confirmada exitosamente: bookingId={}, status={}",
                    response.id(), response.status());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al confirmar reserva: bookingId={}", bookingId, e);
            throw e;
        }
    }

    /**
     * DELETE /api/v1/bookings/{bookingId}
     * cancela el viaje
     */
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable String bookingId) {

        log.info("Recibido DELETE /api/v1/bookings/{}: bookingId={}", bookingId, bookingId);

        try {
            cancelBookingService.execute(bookingId);
            log.info("Reserva cancelada exitosamente: bookingId={}", bookingId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error al cancelar reserva: bookingId={}", bookingId, e);
            throw e;
        }
    }
}
