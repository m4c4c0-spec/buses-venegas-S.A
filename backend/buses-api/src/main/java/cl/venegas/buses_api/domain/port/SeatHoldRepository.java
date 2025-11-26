package cl.venegas.buses_api.domain.port;

import cl.venegas.buses_api.domain.model.SeatHold;
import java.time.LocalDateTime;
import java.util.List;

import cl.venegas.buses_api.domain.model.valueobject.SeatNumber;

public interface SeatHoldRepository {

  SeatHold hold(Long tripId, SeatNumber seat, Long userId, LocalDateTime expiresAt);

  SeatHold save(SeatHold seatHold);

  List<SeatHold> saveAll(List<SeatHold> seatHolds);

  List<SeatHold> findByTripIdAndSeatIn(Long tripId, List<SeatNumber> seatNumbers);

  List<SeatHold> findByTripIdAndSeatNumberIn(Long tripId, List<SeatNumber> seatNumbers);

  void deleteAll(List<SeatHold> seatHolds);
}