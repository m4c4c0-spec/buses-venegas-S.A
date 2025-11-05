package cl.venegas.buses_api.application.exception;

public class SeatAlreadyHeldException extends RuntimeException {
    public SeatAlreadyHeldException(String message) {
        super(message);
    }
}