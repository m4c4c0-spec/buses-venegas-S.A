package cl.venegas.buses_api.domain.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private String id; // e.g. BB-123456

    private String origen;
    private String destino;
    private String fechaViaje;
    private String horarioSalida;
    private String horarioLlegada;
    private String emailContacto;
    private Integer precioTotal;
    
    // Si hay viaje de vuelta
    private Boolean idaYVuelta;
    private String fechaVuelta;

    @Column(columnDefinition = "TEXT")
    private String pasajerosJson; // Guardamos la lista de pasajeros y asientos en JSON por simplicidad

    @CreationTimestamp
    private LocalDateTime createdAt;
}
