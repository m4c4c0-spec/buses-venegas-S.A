package cl.venegas.buses_api.application.usecase.seat;

import org.springframework.stereotype.Service;

import cl.venegas.buses_api.domain.model.entity.SeatHold;
import cl.venegas.buses_api.domain.repository.SeatHoldRepository;

import java.time.LocalDateTime;

@Service
public class HoldSeatsService {
  private final SeatHoldRepository holds;
  private final long HOLD_DURATION_MINUTES = 10;

  public HoldSeatsService(SeatHoldRepository holds) {
    this.holds = holds;
  }

  public java.util.List<SeatHold> execute(cl.venegas.buses_api.application.Command.HoldSeatsCommand command) {

    var expiresAt = LocalDateTime.now().plusMinutes(HOLD_DURATION_MINUTES);

    return command.seats().stream()
        .map(seat -> holds.hold(command.tripId(), seat, command.userId(), expiresAt))
        .collect(java.util.stream.Collectors.toList());
  }
}