package cl.venegas.buses_api.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    private Long id;
    private Long userId;
    private Long tripId;
    private List<String> seats;  
    private List<Passenger> passengers;
    private BookingStatus status;  
    private BigDecimal totalAmount;
    private String paymentReference;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}