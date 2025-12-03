package cl.venegas.buses_api.domain.exception;

/**
 * Comentario de funcionamiento:
 * esta exsepcion se lanza cuando se intenta reservar un asiento ya ocupado ya
 * que aplica una de las reglas de negocios
 * basicas que solo se reflejan en el domain.
 * 
 */

public class SeatAlreadyHeldException extends RuntimeException {

    private final String seatNumber;
    private final String tripId;

    // Constructor principal con mensaje personalizado
    public SeatAlreadyHeldException(String message) {
        super(message);
        this.seatNumber = null;
        this.tripId = null;
    }

    public SeatAlreadyHeldException(String message, Throwable cause) {
        super(message, cause);
        this.seatNumber = null;
        this.tripId = null;
    }

    // Constructor con detalles del asiento y viaje
    public SeatAlreadyHeldException(String seatNumber, String tripId) {
        super(String.format("El asiento %s ya está reservado para el viaje %s", seatNumber, tripId));
        this.seatNumber = seatNumber;
        this.tripId = tripId;
    }

    // Constructor completo con detalles y causa
    public SeatAlreadyHeldException(String seatNumber, String tripId, Throwable cause) {
        super(String.format("El asiento %s ya está reservado para el viaje %s", seatNumber, tripId), cause);
        this.seatNumber = seatNumber;
        this.tripId = tripId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public String getTripId() {
        return tripId;
    }
}
