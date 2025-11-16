package cl.app.domain.repository;

import cl.app.domain.model.entity.Booking;
import cl.app.domain.model.entity.Bus;
import cl.app.domain.model.valueobject.BusStatus;

import java.util.List;
import java.util.Optional;


public interface BusRepository {
    Bus save(Bus bus);
    Optional<Bus> findById(Long id);
    Optional<Bus> findByPlateNumber(String plateNumber);
    List<Bus> findByStatus(BusStatus status);
    List<Bus> findAll();
}