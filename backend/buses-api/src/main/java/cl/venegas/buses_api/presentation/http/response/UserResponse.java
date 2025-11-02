package cl.venegas.buses_api.presentation.http.response;

import cl.venegas.buses_api.domain.model.User;
import cl.venegas.buses_api.domain.model.UserRole;

public record UserResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        String phone,
        UserRole role,
        String createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.id(),
                user.email(),
                user.firstName(),
                user.lastName(),
                user.phone(),
                user.role(),
                user.createdAt().toString()
        );
    }
}