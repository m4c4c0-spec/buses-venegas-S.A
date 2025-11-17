package cl.venegas.buses_api.application.usecase.seat;


import org.springframework.stereotype.Service;

import cl.venegas.buses_api.domain.model.entity.SeatHold;
import cl.venegas.buses_api.domain.repository.SeatHoldRepository;

import java.time.LocalDateTime;


@Service
public class HoldSeatsService {
  private final SeatHoldRepository holds;
  private final long HOLD_DURATION_MINUTES = 10; 

  public HoldSeatsService(SeatHoldRepository holds){ 
    this.holds = holds; 
  }

  public SeatHold handle(Long tripId, String seat, Long userId){
   
    var expiresAt = LocalDateTime.now().plusMinutes(HOLD_DURATION_MINUTES);
    
    return holds.hold(tripId, seat, userId, expiresAt);
  }

  


}