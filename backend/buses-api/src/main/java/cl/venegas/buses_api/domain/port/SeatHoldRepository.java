package cl.venegas.buses_api.domain.port;

public interface SeatHoldRepository {

  LocalDateTime hold(Long tripId, String seat, Long userId, LocalDateTime expiresAt);
}
