package cl.venegas.buses_api.infrastructure.persistence.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import cl.venegas.buses_api.domain.model.Booking;
import cl.venegas.buses_api.domain.model.BookingStatus;
import cl.venegas.buses_api.domain.port.BookingRepository;
import cl.venegas.buses_api.infrastructure.persistence.jpa.entity.BookingJpa;
import cl.venegas.buses_api.infrastructure.persistence.jpa.mapper.BookingMapper;
import cl.venegas.buses_api.infrastructure.persistence.jpa.repo.BookingJpaRepository;

@Component
public class BookingRepositoryJpa implements BookingRepository {

    private final BookingJpaRepository repo;
    private final BookingMapper mapper;

    public BookingRepositoryJpa(BookingJpaRepository repo, BookingMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public Booking save(Booking booking) {
        BookingJpa jpa = mapper.toEntity(booking);
        BookingJpa saved = repo.save(jpa);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Booking> findById(Long id) {
        return repo.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Booking> findByUserId(Long userId) {
        return repo.findByUserId(userId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void delete(Booking booking) {
        if (booking.getId() != null) {
            repo.deleteById(booking.getId());
        }
    }

    @Override
    public void updateStatus(Long bookingId, BookingStatus status) {
        repo.findById(bookingId).ifPresent(booking -> {
            booking.setStatus(status);
            repo.save(booking);
        });
    }

    @Override
    public List<Booking> findByStatus(BookingStatus status) {
        return repo.findByStatus(status)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Booking> findByTripId(Long tripId) {
        return repo.findByTripId(tripId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}