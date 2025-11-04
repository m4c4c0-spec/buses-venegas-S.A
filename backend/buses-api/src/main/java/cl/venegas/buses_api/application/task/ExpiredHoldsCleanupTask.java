package cl.venegas.buses_api.application.task;

import cl.venegas.buses_api.infrastructure.persistence.jpa.repo.SeatHoldJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class ExpiredHoldsCleanupTask {

    private static final Logger log = LoggerFactory.getLogger(ExpiredHoldsCleanupTask.class);
    private final SeatHoldJpaRepository seatHoldRepo;

    public ExpiredHoldsCleanupTask(SeatHoldJpaRepository seatHoldRepo) {
        this.seatHoldRepo = seatHoldRepo;
    }

    // Se ejecuta cada 1 minuto (60000 ms)
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cleanupExpiredHolds() {
        log.info("Ejecutando limpieza de reservas expiradas...");
        try {
            seatHoldRepo.deleteByExpiresAtBefore(LocalDateTime.now());
            log.info("Limpieza completada.");
        } catch (Exception e) {
            log.error("Error durante la limpieza de reservas expiradas", e);
        }
    }
}