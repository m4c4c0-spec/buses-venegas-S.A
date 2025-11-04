package cl.venegas.buses_api.application.usecase;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import cl.venegas.buses_api.domain.model.User;
import cl.venegas.buses_api.domain.model.UserRole;
import cl.venegas.buses_api.domain.port.UserRepository;

@Service
public class RegisterUserService {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserService(UserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    public User handle(String email, String password, String firstName,
                       String lastName, String phone) {

        users.findByEmail(email).ifPresent(u -> {
            throw new IllegalArgumentException("Email ya esta registrado");
        });


        User user = new User(
                null,
                email,
                firstName,
                lastName,
                phone,
                UserRole.CLIENTE,
                LocalDateTime.now()
        );

        return users.save(user);
    }
}