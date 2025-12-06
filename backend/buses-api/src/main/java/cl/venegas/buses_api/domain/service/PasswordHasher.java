package cl.venegas.buses_api.domain.service;

/**
 * Interface for password hashing operations.
 */
public interface PasswordHasher {

    /**
     * Encode a raw password.
     * 
     * @param rawPassword the raw password to encode
     * @return the encoded password
     */
    String encode(String rawPassword);

    /**
     * Check if a raw password matches an encoded password.
     * 
     * @param rawPassword     the raw password
     * @param encodedPassword the encoded password
     * @return true if they match
     */
    boolean matches(String rawPassword, String encodedPassword);
}
