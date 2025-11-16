package cl.app.domain.repository;

import cl.app.domain.model.entity.SeatHold;

import java.time.LocalDateTime;
import java.util.List;

public interface SeatHoldRepository {
  
  SeatHold hold(Long tripId, String seat, Long userId, LocalDateTime expiresAt);
  
  SeatHold save(SeatHold seatHold);
  
  List<SeatHold> findByTripIdAndSeatIn(Long tripId, List<String> seatNumbers);
  
  List<SeatHold> findByTripIdAndSeatNumberIn(Long tripId, List<String> seatNumbers);
  
  void deleteAll(List<SeatHold> seatHolds);
}