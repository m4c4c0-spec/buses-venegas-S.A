package cl.venegas.buses_api.domain.port;

import java.util.List;
import java.util.Optional;
import cl.venegas.buses_api.domain.model.Bus;
import cl.venegas.buses_api.domain.model.BusStatus;

public interface BusRepository {
    Bus save(Bus bus);
    Optional<Bus> findById(Long id);
    Optional<Bus> findByPlateNumber(String plateNumber);
    List<Bus> findByStatus(BusStatus status);
    List<Bus> findAll();
}