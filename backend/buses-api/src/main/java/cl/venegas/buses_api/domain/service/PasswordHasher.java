package cl.venegas.buses_api.domain.service;

public interface PasswordHasher {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
