package cl.venegas.buses_api.application.usecase.user;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import cl.venegas.buses_api.application.Command.RegisterUserCommand;
import cl.venegas.buses_api.domain.model.entity.User;
import cl.venegas.buses_api.domain.model.valueobject.UserRole;
import cl.venegas.buses_api.domain.repository.UserRepository;
import cl.venegas.buses_api.domain.service.PasswordHasher;

@Service
public class RegisterUserService {
    private final UserRepository users;
    private final PasswordHasher passwordHasher;

    public RegisterUserService(UserRepository users, PasswordHasher passwordHasher) {
        this.users = users;
        this.passwordHasher = passwordHasher;
    }

    public User execute(RegisterUserCommand command) {

        users.findByEmail(command.email()).ifPresent(u -> {
            throw new IllegalArgumentException("Email ya esta registrado");
        });

        String encodedPassword = passwordHasher.encode(command.password());

        User user = new User(
                null,
                command.email(),
                encodedPassword,
                command.firstName(),
                command.lastName(),
                command.phone(),
                UserRole.CLIENTE,
                LocalDateTime.now());

        return users.save(user);
    }
}