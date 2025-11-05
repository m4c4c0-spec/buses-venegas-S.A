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

@Entity
@Table(name="seat_hold", uniqueConstraints=@UniqueConstraint(columnNames={"trip_id","seat"}))
public class SeatHoldJpa {
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Long id;

  @Column(name="trip_id")
  private Long tripId;

  private String seat;
  private Long userId;
  private LocalDateTime expiresAt;

  public SeatHoldJpa() {}

  public SeatHoldJpa(Long tripId, String seat, Long userId, LocalDateTime expiresAt){
    this.tripId=tripId;
    this.seat=seat;
    this.userId=userId;
    this.expiresAt=expiresAt;
  }

  public Long getId(){
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getTripId(){
    return tripId;
  }

  public void setTripId(Long v){
    this.tripId=v;
  }

  public String getSeat(){
    return seat;
  }

  public void setSeat(String v){
    this.seat=v;
  }

  public Long getUserId(){
    return userId;
  }

  public void setUserId(Long v){
    this.userId=v;
  }

  public LocalDateTime getExpiresAt(){
    return expiresAt;
  }

  public void setExpiresAt(LocalDateTime v){
    this.expiresAt=v;
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