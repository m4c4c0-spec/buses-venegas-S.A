package cl.venegas.buses_api.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


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
    
  
    public Booking() {
    }
    
    
    public Booking(Long id, Long userId, Long tripId, List<String> seats, 
                   List<Passenger> passengers, BookingStatus status, 
                   BigDecimal totalAmount, String paymentReference,
                   LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.id = id;
        this.userId = userId;
        this.tripId = tripId;
        this.seats = seats;
        this.passengers = passengers;
        this.status = status;
        this.totalAmount = totalAmount;
        this.paymentReference = paymentReference;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }
    
    
    public Long getId() {
        return id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public Long getTripId() {
        return tripId;
    }
    
    public List<String> getSeats() {
        return seats;
    }
    
    public List<Passenger> getPassengers() {
        return passengers;
    }
    
    public BookingStatus getStatus() {
        return status;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public String getPaymentReference() {
        return paymentReference;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
   
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }
    
    public void setSeats(List<String> seats) {
        this.seats = seats;
    }
    
    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
    }
    
    public void setStatus(BookingStatus status) {
        this.status = status;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}

