package cl.app.domain.model.entity;

import cl.app.domain.model.entity.User;
import cl.app.domain.model.valueobject.UserRole;

import java.time.LocalDateTime;

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