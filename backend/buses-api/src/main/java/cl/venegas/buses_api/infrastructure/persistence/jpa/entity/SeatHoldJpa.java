package cl.venegas.buses_api.infrastructure.persistence.jpa.entity;

import java.time.LocalDateTime;

import cl.venegas.buses_api.domain.model.entity.SeatHold;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "seat_hold", uniqueConstraints = @UniqueConstraint(columnNames = { "trip_id", "seat_number" }))
public class SeatHoldJpa {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "trip_id")
  private Long tripId;

  @Column(name = "seat_number")
  private String seatNumber;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "hold_until")
  private LocalDateTime holdUntil;

  @Column(name = "status")
  private String status = "HELD";

  public SeatHoldJpa() {
  }

  public SeatHoldJpa(Long tripId, String seatNumber, Long userId, LocalDateTime holdUntil) {
    this.tripId = tripId;
    this.seatNumber = seatNumber;
    this.userId = userId;
    this.holdUntil = holdUntil;
    this.status = "HELD";
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getTripId() {
    return tripId;
  }

  public void setTripId(Long v) {
    this.tripId = v;
  }

  public String getSeatNumber() {
    return seatNumber;
  }

  public void setSeatNumber(String v) {
    this.seatNumber = v;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long v) {
    this.userId = v;
  }

  public LocalDateTime getHoldUntil() {
    return holdUntil;
  }

  public void setHoldUntil(LocalDateTime v) {
    this.holdUntil = v;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public static SeatHoldJpa fromDomain(SeatHold seatHold) {
    SeatHoldJpa jpa = new SeatHoldJpa();
    jpa.setId(seatHold.id());
    jpa.setTripId(seatHold.tripId());
    jpa.setSeatNumber(seatHold.seat());
    jpa.setUserId(seatHold.userId());
    jpa.setHoldUntil(seatHold.expiresAt());
    jpa.setStatus("HELD");
    return jpa;
  }

  public SeatHold toDomain() {
    return new SeatHold(
        this.id,
        this.tripId,
        this.seatNumber,
        this.userId,
        this.holdUntil);
  }
}