package cl.venegas.buses_api.interfaces.web;

import cl.venegas.buses_api.domain.model.entity.Reserva;
import cl.venegas.buses_api.domain.model.entity.ReservaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaRepository reservaRepository;

    public ReservaController(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReserva(@PathVariable String id, @RequestParam String rutOrEmail) {
        // En nuestro modelo actual, usamos emailContacto principalmente
        Optional<Reserva> reservaOpt = reservaRepository.findByIdAndEmailContacto(id, rutOrEmail);
        
        if (reservaOpt.isPresent()) {
            return ResponseEntity.ok(reservaOpt.get());
        } else {
            return ResponseEntity.status(404).body("Reserva no encontrada o datos incorrectos.");
        }
    }

    @PutMapping("/{id}/cambiar")
    public ResponseEntity<?> cambiarReserva(@PathVariable String id, @RequestBody Map<String, String> body) {
        Optional<Reserva> reservaOpt = reservaRepository.findById(id);
        
        if (reservaOpt.isPresent()) {
            Reserva reserva = reservaOpt.get();
            reserva.setFechaViaje(body.get("nuevaFecha"));
            reserva.setHorarioSalida(body.get("nuevoHorarioSalida"));
            reserva.setHorarioLlegada(body.get("nuevoHorarioLlegada"));
            // Podríamos validar aquí que la nueva fecha sea >= hoy
            
            reservaRepository.save(reserva);
            return ResponseEntity.ok(reserva);
        } else {
            return ResponseEntity.status(404).body("Reserva no encontrada.");
        }
    }
}
