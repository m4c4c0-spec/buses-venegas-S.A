package cl.venegas.buses_api.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="seat_hold", uniqueConstraints=@UniqueConstraint(columnNames={"trip_id","seat"}))
public class SeatHoldJpa {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(name="trip_id") private Long tripId;
  private String seat; private Long userId; private LocalDateTime expiresAt;

  public SeatHoldJpa() {}
  public SeatHoldJpa(Long tripId, String seat, Long userId, LocalDateTime expiresAt){
    this.tripId=tripId; this.seat=seat; this.userId=userId; this.expiresAt=expiresAt;
  }
  public Long getId(){ 
    return id;
}
  public Long getTripId(){
    return tripId;
} public void setTripId(Long v){
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
  public LocalDateTime getExpiresAt(){return expiresAt;} public void setExpiresAt(LocalDateTime v){
    this.expiresAt=v;
}
}