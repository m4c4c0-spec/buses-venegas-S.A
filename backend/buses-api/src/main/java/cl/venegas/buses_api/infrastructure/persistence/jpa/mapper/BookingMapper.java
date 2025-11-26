package cl.venegas.buses_api.infrastructure.persistence.jpa.mapper;

import cl.venegas.buses_api.domain.model.Booking;
import cl.venegas.buses_api.domain.model.Passenger;
import cl.venegas.buses_api.domain.model.valueobject.Money;
import cl.venegas.buses_api.domain.model.valueobject.SeatNumber;
import cl.venegas.buses_api.infrastructure.persistence.jpa.entity.BookingJpa;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingMapper {

    private final ObjectMapper objectMapper;

    public BookingMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Booking toDomain(BookingJpa entity) {
        if (entity == null) {
            return null;
        }
        try {
            List<Passenger> passengers = objectMapper.readValue(
                    entity.getPassengersJson(),
                    new TypeReference<List<Passenger>>() {
                    });

            List<SeatNumber> seats = Arrays.stream(entity.getSeats())
                    .map(SeatNumber::new)
                    .collect(Collectors.toList());

            return new Booking(
                    entity.getId(),
                    entity.getUserId(),
                    entity.getTripId(),
                    seats,
                    passengers,
                    entity.getStatus(),
                    new Money(entity.getTotalAmount()),
                    entity.getPaymentReference(),
                    entity.getCreatedAt(),
                    entity.getExpiresAt());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error mapping BookingJpa to Domain", e);
        }
    }

    public BookingJpa toEntity(Booking domain) {
        if (domain == null) {
            return null;
        }
        try {
            String passengersJson = objectMapper.writeValueAsString(domain.getPassengers());
            String[] seats = domain.getSeats().stream()
                    .map(SeatNumber::getValue)
                    .toArray(String[]::new);

            return new BookingJpa(
                    domain.getId(),
                    domain.getUserId(),
                    domain.getTripId(),
                    seats,
                    passengersJson,
                    domain.getStatus(),
                    domain.getTotalAmount().getAmount(),
                    domain.getPaymentReference(),
                    domain.getCreatedAt(),
                    domain.getExpiresAt());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error mapping Booking to Entity", e);
        }
    }
}
