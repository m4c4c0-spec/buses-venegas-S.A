package cl.venegas.buses_api.domain.port;
import java.time.LocalDateTime;

public interface SeatHoldRepository {

  LocalDateTime hold(Long tripId, String seat, Long userId, LocalDateTime expiresAt);
}
