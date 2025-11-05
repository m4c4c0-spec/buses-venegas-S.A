package cl.venegas.buses_api.presentation.http.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import cl.venegas.buses_api.application.exception.SeatAlreadyHeldException;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(SeatAlreadyHeldException.class)
    protected ResponseEntity<Object> handleSeatAlreadyHeld(SeatAlreadyHeldException ex, WebRequest request) {
        // Se devuelve el mismo error 409, pero atrapado en una excepsion
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("error", "seat_taken", "message", ex.getMessage()));
    }

    
}
