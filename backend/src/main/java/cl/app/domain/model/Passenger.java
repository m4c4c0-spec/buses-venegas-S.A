package backend.src.main.java.cl.app.domain.model;

public record Passenger(Long id, String firstName, String lastName, String documentType, String documentNumber, String email, String phone) {
    
}
