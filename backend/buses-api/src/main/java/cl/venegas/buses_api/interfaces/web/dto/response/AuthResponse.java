package cl.venegas.buses_api.interfaces.web.dto.response;

/**
 * DTO de respuesta de autenticación.
 * Contiene el access token y el refresh token.
 */
public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        UserResponse user
) {
    public static AuthResponse of(String accessToken, String refreshToken,
                                   long expiresIn, UserResponse user) {
        return new AuthResponse(accessToken, refreshToken, "Bearer", expiresIn, user);
    }
}
