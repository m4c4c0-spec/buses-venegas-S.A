package cl.venegas.buses_api.infrastructure.persistence.jpa.entity;

import cl.venegas.buses_api.domain.model.Trip;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name="trip")
public class TripJpa {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  private String origin; private String destination;
  private LocalDateTime departureTs; private LocalDateTime arrivalTs;
  private Integer basePriceClp;

  public Trip toDomain(){
    return new Trip(id, origin, destination, departureTs, arrivalTs, basePriceClp);
  }
  public TripJpa() {

  }
  public Long getId(){
    return id;
  }
  public void setId(Long id){
    this.id=id;
  }
  public String getOrigin(){
    return origin;
  }
  public void setOrigin(String v){
    this.origin=v;
  }
  public String getDestination(){
    return destination;
  }
  public void setDestination(String v){
    this.destination=v;
  }
  public LocalDateTime getDepartureTs(){
    return departureTs;
  }
  public void setDepartureTs(LocalDateTime v){
    this.departureTs=v;
  }
  public LocalDateTime getArrivalTs(){
    return arrivalTs;
  }
  public void setArrivalTs(LocalDateTime v){
    this.arrivalTs=v;
  }
  public Integer getBasePriceClp(){
    return basePriceClp;
  }
  public void setBasePriceClp(Integer v){
    this.basePriceClp=v;
  }
}