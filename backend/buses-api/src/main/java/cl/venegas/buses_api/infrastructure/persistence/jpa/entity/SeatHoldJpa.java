package cl.venegas.buses_api.infrastructure.persistence.jpa.entity;

import java.time.LocalDateTime;

import cl.venegas.buses_api.domain.model.SeatHold;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "seat_holds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatHoldJpa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "trip_id", nullable = false)
    private Long tripId;
    
    @Column(name = "seat_number", nullable = false)
    private String seatNumber;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    // Convertir de JPA a Domain
    public SeatHold toDomain() {
        return new SeatHold(
            id,
            tripId,
            seatNumber,
            userId,
            expiresAt
        );
    }
    
    // Convertir de Domain a JPA
    public static SeatHoldJpa fromDomain(SeatHold seatHold) {
        SeatHoldJpa jpa = new SeatHoldJpa();
        jpa.setId(seatHold.getId());
        jpa.setTripId(seatHold.getTripId());
        jpa.setSeatNumber(seatHold.getSeatNumber());
        jpa.setUserId(seatHold.getUserId());
        jpa.setExpiresAt(seatHold.getExpiresAt());
        return jpa;
    }
}