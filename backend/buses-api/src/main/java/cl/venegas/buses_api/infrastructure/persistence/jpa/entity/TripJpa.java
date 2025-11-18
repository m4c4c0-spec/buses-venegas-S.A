package cl.venegas.buses_api.infrastructure.persistence.jpa.entity;

import cl.venegas.buses_api.domain.model.Trip;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "trip")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TripJpa {
  @Id 
  @GeneratedValue(strategy = GenerationType.IDENTITY) 
  private Long id;
  
  private String origin; 
  private String destination;
  private LocalDateTime departureTs; 
  private LocalDateTime arrivalTs;
  private Integer basePriceClp;

  public Trip toDomain() {
    return new Trip(id, origin, destination, departureTs, arrivalTs, basePriceClp);
  }
}