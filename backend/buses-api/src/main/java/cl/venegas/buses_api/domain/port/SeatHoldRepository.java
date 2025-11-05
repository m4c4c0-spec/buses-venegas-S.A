package cl.venegas.buses_api.domain.port;

import cl.venegas.buses_api.domain.model.SeatHold;
import java.time.LocalDateTime;
import java.util.List;

public interface SeatHoldRepository {
  
  SeatHold hold(Long tripId, String seat, Long userId, LocalDateTime expiresAt);
  
  SeatHold save(SeatHold seatHold);
  
  List<SeatHold> findByTripIdAndSeatIn(Long tripId, List<String> seatNumbers);
  
  List<SeatHold> findByTripIdAndSeatNumberIn(Long tripId, List<String> seatNumbers);
  
  void deleteAll(List<SeatHold> seatHolds);
}