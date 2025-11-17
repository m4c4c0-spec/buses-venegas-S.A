package cl.venegas.buses_api.domain.exception;


/**
Comentario de funcionamiento:
esta exsepcion se lanza cuando se intenta reservar un asiento ya ocupado ya que aplica una de las reglas de negocios 
basicas que solo se reflejan en el domain.  

 */

public class SeatAlreadyHeldException extends RuntimeException {
    
    private final String seatNumber;
    private final String tripId;

      public SeatAlreadyHeldException(String seatNumber, String tripId) {
        super(String.format("Este asiento ya fue reservado ", seatNumber, tripId));
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


