package cl.app.domain.model.entity;

import cl.app.domain.model.entity.Passenger;

public record Passenger(Long id, String firstName, String lastName, String documentType, String documentNumber, String email, String phone) {
    
}
