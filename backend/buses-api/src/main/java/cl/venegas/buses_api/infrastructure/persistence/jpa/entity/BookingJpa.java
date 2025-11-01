package cl.venegas.buses_api.infrastructure.persistence.jpa.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cl.venegas.buses_api.domain.model.Booking;
import cl.venegas.buses_api.domain.model.BookingStatus;
import cl.venegas.buses_api.domain.model.Passenger;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "trip_id", nullable = false)
    private Long tripId;

    @Column(name = "seats", columnDefinition = "text[]", nullable = false)
    private String[] seats;

    @Column(name = "passengers", columnDefinition = "jsonb", nullable = false)
    private String passengersJson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "payment_reference")
    private String paymentReference;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Booking toDomain() {
        try {
            List<Passenger> passengers = objectMapper.readValue(
                    passengersJson,
                    new TypeReference<List<Passenger>>() {}
            );

            return new Booking(
                    id,
                    userId,
                    tripId,
                    List.of(seats),
                    passengers,
                    status,
                    totalAmount,
                    paymentReference,
                    createdAt,
                    expiresAt
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing passengers JSON", e);
        }
    }
}