package backend.src.main.java.cl.app.presentation.http.response;

import backend.src.main.java.cl.app.domain.model.User;
import backend.src.main.java.cl.app.domain.model.UserRole;

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