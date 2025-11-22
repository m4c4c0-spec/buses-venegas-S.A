package cl.venegas.buses_api.infrastructure.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import cl.venegas.buses_api.domain.service.PasswordHasher;

@Component
public class SpringPasswordHasher implements PasswordHasher {

    private final PasswordEncoder passwordEncoder;

    public SpringPasswordHasher(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
