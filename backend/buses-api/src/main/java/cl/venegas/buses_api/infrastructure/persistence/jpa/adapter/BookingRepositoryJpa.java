package cl.venegas.buses_api.infrastructure.persistence.jpa.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import cl.venegas.buses_api.domain.model.entity.Booking;
import cl.venegas.buses_api.domain.model.valueobject.BookingStatus;
import cl.venegas.buses_api.domain.repository.BookingRepository;
import cl.venegas.buses_api.infrastructure.persistence.jpa.entity.BookingJpa;
import cl.venegas.buses_api.infrastructure.persistence.jpa.repo.BookingJpaRepository;

@Component
public class BookingRepositoryJpa implements BookingRepository {

    private final BookingJpaRepository repo;

    public BookingRepositoryJpa(BookingJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public Booking save(Booking booking) {
        BookingJpa jpa = BookingJpa.fromDomain(booking);
        BookingJpa saved = repo.save(jpa);
        return saved.toDomain();
    }

    @Override
    public Optional<Booking> findById(Long id) {
        return repo.findById(id)
                .map(BookingJpa::toDomain);
    }

    @Override
    public List<Booking> findByUserId(Long userId) {
        return repo.findByUserId(userId)
                .stream()
                .map(BookingJpa::toDomain)
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
    public List<Booking> findByTripId(Long tripId) {
        return repo.findByTripId(tripId)
                .stream()
                .map(BookingJpa::toDomain)
                .toList();
    }

    @Override
    public List<Booking> findByStatus(BookingStatus status) {
        throw new UnsupportedOperationException("Unimplemented method 'findByStatus'");
    }
}