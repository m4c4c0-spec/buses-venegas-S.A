package cl.venegas.buses_api.domain.repository;

import cl.venegas.buses_api.domain.model.entity.Bus;
import cl.venegas.buses_api.domain.model.valueobject.BusStatus;

import java.util.List;
import java.util.Optional;



public interface BusRepository {
    Bus save(Bus bus);
    Optional<Bus> findById(Long id);
    Optional<Bus> findByPlateNumber(String plateNumber);
    List<Bus> findByStatus(BusStatus status);
    List<Bus> findAll();
}