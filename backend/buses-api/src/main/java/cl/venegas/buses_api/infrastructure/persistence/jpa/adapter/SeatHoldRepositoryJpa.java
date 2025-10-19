package cl.venegas.buses_api.infrastructure.persistence.jpa.adapter;

import cl.venegas.buses_api.domain.port.SeatHoldRepository;
import cl.venegas.buses_api.infrastructure.persistence.jpa.entity.SeatHoldJpa;
import cl.venegas.buses_api.infrastructure.persistence.jpa.repo.SeatHoldJpaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import cl.venegas.buses_api.application.exception.SeatAlreadyHeldException;

import java.time.LocalDateTime;

@Component
public class SeatHoldRepositoryJpa implements SeatHoldRepository {
  private final SeatHoldJpaRepository repo;
  public SeatHoldRepositoryJpa(SeatHoldJpaRepository repo){ this.repo=repo; }

 @Override @Transactional
public LocalDateTime hold(Long tripId, String seat, Long userId, LocalDateTime expires) { 
  try {
    
    repo.save(new SeatHoldJpa(tripId, seat, userId, expires)); 
    return expires;
  } catch (DataIntegrityViolationException e){
    throw new SeatAlreadyHeldException("El asiento " + seat + " para el viaje " + tripId + " ya está reservado.");
  }
}
  
}