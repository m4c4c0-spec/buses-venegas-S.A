package cl.venegas.buses_api.domain.model.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, String> {
    Optional<Reserva> findByIdAndEmailContacto(String id, String emailContacto);
}
