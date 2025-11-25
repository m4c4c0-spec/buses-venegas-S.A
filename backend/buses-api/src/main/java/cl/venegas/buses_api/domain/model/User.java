package cl.venegas.buses_api.domain.model;

import cl.venegas.buses_api.domain.model.valueobject.Email;
import cl.venegas.buses_api.domain.model.valueobject.Password;
import java.time.LocalDateTime;

public record User(
                Long id,
                Email email,
                Password password,
                String firstName,
                String lastName,
                String phone,
                UserRole role,
                LocalDateTime createdAt) {
}