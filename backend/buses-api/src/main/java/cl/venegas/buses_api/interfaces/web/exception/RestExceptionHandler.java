package cl.venegas.buses_api.interfaces.web.exception;

import cl.venegas.buses_api.domain.exception.SeatAlreadyHeldException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador global de excepciones para la API REST.
 * 
 * Detecta las excepciones lanzadas por los controladores y las convierte
 * en respuestas HTTP adecuadas.
 */
@RestControllerAdvice
public class RestExceptionHandler {

        // Maneja la validacion de errores (Bean Validation)

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationErrors(
                        MethodArgumentNotValidException ex) {

                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getAllErrors().forEach(error -> {
                        String fieldName = ((FieldError) error).getField();
                        String errorMessage = error.getDefaultMessage();
                        errors.put(fieldName, errorMessage);
                });

                ErrorResponse response = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Validacion fallida",
                                errors,
                                LocalDateTime.now());

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Maneja la excepcion de dominio: Asiento ya reservado

        @ExceptionHandler(SeatAlreadyHeldException.class)
        public ResponseEntity<ErrorResponse> handleSeatAlreadyHeld(
                        SeatAlreadyHeldException ex) {

                ErrorResponse response = new ErrorResponse(
                                HttpStatus.CONFLICT.value(),
                                ex.getMessage(),
                                Map.of(
                                                "numero de asiento", ex.getSeatNumber(),
                                                "id del viaje", ex.getTripId()),
                                LocalDateTime.now());

                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        // Maneja las excepciones generales
        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<ErrorResponse> handleRuntimeException(
                        RuntimeException ex) {

                ErrorResponse response = new ErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "Un error inesperado ocurrio: " + ex.getMessage(),
                                null,
                                LocalDateTime.now());

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        // Maneja las excepciones generales
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGenericException(
                        Exception ex) {

                ErrorResponse response = new ErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "Un error ocurrio: " + ex.getMessage(),
                                null,
                                LocalDateTime.now());

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        // DTO de respuesta de error
        public record ErrorResponse(
                        int status,
                        String message,
                        Map<String, String> details,
                        LocalDateTime timestamp) {
        }
}
