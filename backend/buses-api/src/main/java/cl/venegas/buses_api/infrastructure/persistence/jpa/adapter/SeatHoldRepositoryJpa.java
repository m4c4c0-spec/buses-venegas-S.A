package cl.venegas.buses_api.infrastructure.persistence.jpa.adapter;

import cl.venegas.buses_api.domain.model.SeatHold;
import cl.venegas.buses_api.domain.model.valueobject.SeatNumber;
import cl.venegas.buses_api.domain.port.SeatHoldRepository;
import cl.venegas.buses_api.infrastructure.persistence.jpa.entity.SeatHoldJpa;
import cl.venegas.buses_api.infrastructure.persistence.jpa.mapper.SeatHoldMapper;
import cl.venegas.buses_api.infrastructure.persistence.jpa.repo.SeatHoldJpaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import cl.venegas.buses_api.application.exception.SeatAlreadyHeldException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SeatHoldRepositoryJpa implements SeatHoldRepository {
  private final SeatHoldJpaRepository repo;
  private final SeatHoldMapper mapper;

  public SeatHoldRepositoryJpa(SeatHoldJpaRepository repo, SeatHoldMapper mapper) {
    this.repo = repo;
    this.mapper = mapper;
  }

  @Override
  @Transactional
  public SeatHold hold(Long tripId, SeatNumber seat, Long userId, LocalDateTime expiresAt) {
    try {
      SeatHold seatHold = new SeatHold(null, tripId, seat, userId, expiresAt);
      SeatHoldJpa jpa = mapper.toEntity(seatHold);
      SeatHoldJpa saved = repo.save(jpa);
      return mapper.toDomain(saved);
    } catch (DataIntegrityViolationException e) {
      throw new SeatAlreadyHeldException("El asiento " + seat + " para el viaje " + tripId + " ya está reservado.");
    }
  }

  @Override
  public SeatHold save(SeatHold seatHold) {
    SeatHoldJpa jpa = mapper.toEntity(seatHold);
    SeatHoldJpa saved = repo.save(jpa);
    return mapper.toDomain(saved);
  }

  @Override
  public List<SeatHold> saveAll(List<SeatHold> seatHolds) {
    List<SeatHoldJpa> jpaList = seatHolds.stream()
        .map(mapper::toEntity)
        .collect(Collectors.toList());
    return repo.saveAll(jpaList).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<SeatHold> findByTripIdAndSeatIn(Long tripId, List<SeatNumber> seatNumbers) {
    List<String> seats = seatNumbers.stream()
        .map(SeatNumber::getValue)
        .collect(Collectors.toList());
    return repo.findByTripIdAndSeatIn(tripId, seats)
        .stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public List<SeatHold> findByTripIdAndSeatNumberIn(Long tripId, List<SeatNumber> seatNumbers) {
    return findByTripIdAndSeatIn(tripId, seatNumbers);
  }

  @Override
  @Transactional
  public void deleteAll(List<SeatHold> seatHolds) {
    List<SeatHoldJpa> jpaList = seatHolds.stream()
        .map(mapper::toEntity)
        .toList();
    repo.deleteAll(jpaList);
  }
}