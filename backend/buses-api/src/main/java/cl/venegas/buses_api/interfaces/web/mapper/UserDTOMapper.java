package cl.venegas.buses_api.interfaces.web.mapper;

import org.springframework.stereotype.Component;

import cl.venegas.buses_api.application.Command.RegisterUserCommand;
import cl.venegas.buses_api.domain.model.entity.User;
import cl.venegas.buses_api.interfaces.web.dto.request.RegisterUserRequest;
import cl.venegas.buses_api.interfaces.web.dto.response.UserResponse;

@Component
public class UserDTOMapper {

    public RegisterUserCommand toRegisterCommand(RegisterUserRequest request) {
        return new RegisterUserCommand(
                request.email(),
                request.password(),
                request.firstName(),
                request.lastName(),
                request.phone());
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                String.valueOf(user.id()),
                user.email(),
                user.firstName(),
                user.lastName(),
                user.phone(),
                user.role().name(),
                user.createdAt());
    }
}