package cl.venegas.buses_api.interfaces.web;

import cl.venegas.buses_api.domain.model.entity.Reserva;
import cl.venegas.buses_api.domain.model.entity.ReservaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaRepository reservaRepository;
    private final cl.venegas.buses_api.application.usecase.email.EmailService emailService;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    public ReservaController(ReservaRepository reservaRepository, cl.venegas.buses_api.application.usecase.email.EmailService emailService, com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
        this.reservaRepository = reservaRepository;
        this.emailService = emailService;
        this.objectMapper = objectMapper;
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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> anularReserva(@PathVariable String id, @RequestParam String rutOrEmail) {
        Optional<Reserva> reservaOpt = reservaRepository.findByIdAndEmailContacto(id, rutOrEmail);
        
        if (reservaOpt.isPresent()) {
            Reserva reserva = reservaOpt.get();

            // Verificar que el correo coincida (validación de seguridad básica)
            if (!reserva.getEmailContacto().equalsIgnoreCase(rutOrEmail)) {
                return ResponseEntity.status(403).body("El correo proporcionado no coincide con el dueño de la reserva.");
            }

            emailService.sendCancellationEmail(reserva);
            reservaRepository.delete(reserva);
            
            return ResponseEntity.ok("Reserva " + id + " anulada exitosamente.");
        } else {
            return ResponseEntity.status(404).body("Reserva no encontrada o datos incorrectos.");
        }
    }

    @PutMapping("/{id}/cambiar")
    public ResponseEntity<?> cambiarReserva(@PathVariable String id, @RequestBody Map<String, Object> body) {
        Optional<Reserva> reservaOpt = reservaRepository.findById(id);
        
        if (reservaOpt.isPresent()) {
            try {
                Reserva reserva = reservaOpt.get();
                reserva.setFechaViaje((String) body.get("nuevaFecha"));
                reserva.setHorarioSalida((String) body.get("nuevoHorarioSalida"));
                reserva.setHorarioLlegada((String) body.get("nuevoHorarioLlegada"));
                
                // Actualizar asientos
                List<Integer> nuevosAsientos = (List<Integer>) body.get("nuevosAsientos");
                if (nuevosAsientos != null && !nuevosAsientos.isEmpty()) {
                    List<Map<String, Object>> pasajeros = objectMapper.readValue(
                        reserva.getPasajerosJson(), 
                        new com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>>() {}
                    );
                    for (int i = 0; i < pasajeros.size() && i < nuevosAsientos.size(); i++) {
                        pasajeros.get(i).put("asiento", nuevosAsientos.get(i));
                    }
                    reserva.setPasajerosJson(objectMapper.writeValueAsString(pasajeros));
                }
                
                reservaRepository.save(reserva);
                
                // Enviar correo de actualización
                emailService.sendChangeReceiptEmail(reserva);
                
                return ResponseEntity.ok(reserva);
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Error interno actualizando la reserva.");
            }
        } else {
            return ResponseEntity.status(404).body("Reserva no encontrada.");
        }
    }
}
