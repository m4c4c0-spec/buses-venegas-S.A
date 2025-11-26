package cl.venegas.buses_api.infrastructure.persistence.jpa.adapter;

import cl.venegas.buses_api.domain.model.SeatHold;
import cl.venegas.buses_api.domain.port.SeatHoldRepository;
import cl.venegas.buses_api.infrastructure.persistence.jpa.entity.SeatHoldJpa;
import cl.venegas.buses_api.infrastructure.persistence.jpa.repo.SeatHoldJpaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import cl.venegas.buses_api.application.exception.SeatAlreadyHeldException;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SeatHoldRepositoryJpa implements SeatHoldRepository {
  private final SeatHoldJpaRepository repo;

  public SeatHoldRepositoryJpa(SeatHoldJpaRepository repo) {
    this.repo = repo;
  }

  @Override
  @Transactional
  public SeatHold hold(Long tripId, String seat, Long userId, LocalDateTime expiresAt) {
    try {
      SeatHold seatHold = new SeatHold(null, tripId, seat, userId, expiresAt);
      SeatHoldJpa jpa = SeatHoldJpa.fromDomain(seatHold);
      SeatHoldJpa saved = repo.save(jpa);
      return saved.toDomain();
    } catch (DataIntegrityViolationException e) {
      throw new SeatAlreadyHeldException("El asiento " + seat + " para el viaje " + tripId + " ya está reservado.");
    }
  }

  @Override
  public SeatHold save(SeatHold seatHold) {
    SeatHoldJpa jpa = SeatHoldJpa.fromDomain(seatHold);
    SeatHoldJpa saved = repo.save(jpa);
    return saved.toDomain();
  }

  @Override
  public List<SeatHold> findByTripIdAndSeatIn(Long tripId, List<String> seatNumbers) {
    return repo.findByTripIdAndSeatIn(tripId, seatNumbers)
        .stream()
        .map(SeatHoldJpa::toDomain)
        .toList();
  }

  @Override
  public List<SeatHold> findByTripIdAndSeatNumberIn(Long tripId, List<String> seatNumbers) {
    return findByTripIdAndSeatIn(tripId, seatNumbers);
  }

  @Override
  @Transactional
  public void deleteAll(List<SeatHold> seatHolds) {
    List<SeatHoldJpa> jpaList = seatHolds.stream()
        .map(SeatHoldJpa::fromDomain)
        .toList();
    repo.deleteAll(jpaList);
  }

  @Override
  @Transactional
  public void saveAll(List<SeatHold> seatHolds) {
    List<SeatHoldJpa> jpaList = seatHolds.stream()
        .map(SeatHoldJpa::fromDomain)
        .toList();
    repo.saveAll(jpaList);
  }
}