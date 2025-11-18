package cl.venegas.buses_api.infrastructure.persistence.jpa.entity;

import java.time.LocalDateTime;

import cl.venegas.buses_api.domain.model.SeatHold;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "seat_hold", uniqueConstraints = @UniqueConstraint(columnNames = {"trip_id", "seat"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatHoldJpa {
  @Id 
  @GeneratedValue(strategy = GenerationType.IDENTITY) 
  private Long id;
  
  @Column(name = "trip_id") 
  private Long tripId;
  
  private String seat; 
  private Long userId; 
  private LocalDateTime expiresAt;
  
  // Constructor personalizado sin el id (para nuevas entidades)
  public SeatHoldJpa(Long tripId, String seat, Long userId, LocalDateTime expiresAt) {
    this.tripId = tripId; 
    this.seat = seat; 
    this.userId = userId; 
    this.expiresAt = expiresAt;
  }
  
  public static SeatHoldJpa fromDomain(SeatHold seatHold) {
    SeatHoldJpa jpa = new SeatHoldJpa();
    jpa.setId(seatHold.id());
    jpa.setTripId(seatHold.tripId());
    jpa.setSeat(seatHold.seat());
    jpa.setUserId(seatHold.userId());
    jpa.setExpiresAt(seatHold.expiresAt());
    return jpa;
  }
  
  public SeatHold toDomain() {
    return new SeatHold(
      this.id,
      this.tripId,
      this.seat,
      this.userId,
      this.expiresAt
    );
  }
}