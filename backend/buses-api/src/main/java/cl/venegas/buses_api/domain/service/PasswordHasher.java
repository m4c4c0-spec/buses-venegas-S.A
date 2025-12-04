package cl.venegas.buses_api.domain.service;

/**
 * Interface for password hashing operations
 */
public interface PasswordHasher {
    String hash(String rawPassword);

    boolean matches(String rawPassword, String hashedPassword);
}
