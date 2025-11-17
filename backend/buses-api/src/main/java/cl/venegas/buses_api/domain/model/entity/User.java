package cl.venegas.buses_api.domain.model.entity;

import java.time.LocalDateTime;

import cl.venegas.buses_api.domain.model.valueobject.UserRole;

public record User(
        Long id,
        String email,
        String firstName,
        String lastName,
        String phone,
        UserRole role,
        LocalDateTime createdAt
) {
}