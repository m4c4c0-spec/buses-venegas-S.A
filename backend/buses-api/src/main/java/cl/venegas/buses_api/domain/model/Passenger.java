package cl.venegas.buses_api.domain.model;

public record Passenger(Long id, String firstName, String lastName, String documentType, String documentNumber, String email, String phone) {
    
}
