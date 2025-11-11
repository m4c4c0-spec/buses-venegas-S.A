package backend.src.main.java.cl.app.domain.port;

import java.util.List;
import java.util.Optional;
import backend.src.main.java.cl.app.domain.model.Bus;
import backend.src.main.java.cl.app.domain.model.BusStatus;

public interface BusRepository {
    Bus save(Bus bus);
    Optional<Bus> findById(Long id);
    Optional<Bus> findByPlateNumber(String plateNumber);
    List<Bus> findByStatus(BusStatus status);
    List<Bus> findAll();
}