package cl.venegas.buses_api.application.usecase;

import cl.venegas.buses_api.domain.port.SeatHoldRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class HoldSeatsService {
  private final SeatHoldRepository holds;
  public HoldSeatsService(SeatHoldRepository holds){ this.holds = holds; }
  public LocalDateTime handle(Long tripId, String seat, Long userId){
    return holds.hold(tripId, seat, userId);
  }
}