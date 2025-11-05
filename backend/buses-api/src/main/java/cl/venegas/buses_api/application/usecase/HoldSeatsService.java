package cl.venegas.buses_api.application.usecase;

import cl.venegas.buses_api.domain.model.SeatHold;
import cl.venegas.buses_api.domain.port.SeatHoldRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import cl.venegas.buses_api.domain.model.SeatHold;
import cl.venegas.buses_api.domain.port.SeatHoldRepository;

@Service
public class HoldSeatsService {
  private final SeatHoldRepository holds;
  private final long HOLD_DURATION_MINUTES = 10; 

  public HoldSeatsService(SeatHoldRepository holds) { 
    this.holds = holds; 
  }

  public SeatHold handle(Long tripId, String seat, Long userId){
   
    var expiresAt = LocalDateTime.now().plusMinutes(HOLD_DURATION_MINUTES);
    
    return holds.hold(tripId, seat, userId, expiresAt);
  }

  


}