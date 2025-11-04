package cl.venegas.buses_api.domain.model;

import java.time.LocalDateTime;

public record User( Long id, String email, String firstName, String lastName, String phone, UserRole role, LocalDateTime createdAt) {
    
}
