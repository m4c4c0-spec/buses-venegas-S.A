package cl.venegas.buses_api.application.usecase.seat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cl.venegas.buses_api.domain.model.entity.SeatHold;
import cl.venegas.buses_api.domain.repository.SeatHoldRepository;

import java.time.LocalDateTime;

@Service
public class HoldSeatsService {

  private static final Logger log = LoggerFactory.getLogger(HoldSeatsService.class);

  private final SeatHoldRepository holds;
  private final long HOLD_DURATION_MINUTES = 10;

  public HoldSeatsService(SeatHoldRepository holds) {
    this.holds = holds;
  }

  public java.util.List<SeatHold> execute(cl.venegas.buses_api.application.Command.HoldSeatsCommand command) {
    log.info("Iniciando retención de asientos: tripId={}, userId={}, seats={}",
        command.tripId(), command.userId(), command.seats());

    try {
      var expiresAt = LocalDateTime.now().plusMinutes(HOLD_DURATION_MINUTES);

      log.debug("Duración de retención: {} minutos, expira en: {}",
          HOLD_DURATION_MINUTES, expiresAt);

      java.util.List<SeatHold> heldSeats = command.seats().stream()
          .map(seat -> holds.hold(command.tripId(), seat, command.userId(), expiresAt))
          .collect(java.util.stream.Collectors.toList());

      log.info("Asientos retenidos exitosamente: tripId={}, userId={}, cantidad={}, expiresAt={}",
          command.tripId(), command.userId(), heldSeats.size(), expiresAt);

      return heldSeats;
    } catch (Exception e) {
      log.error("Error al retener asientos: tripId={}, userId={}, seats={}",
          command.tripId(), command.userId(), command.seats(), e);
      throw e;
    }
  }
}