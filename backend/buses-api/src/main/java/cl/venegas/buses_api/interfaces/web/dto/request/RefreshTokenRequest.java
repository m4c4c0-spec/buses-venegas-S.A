package cl.venegas.buses_api.interfaces.web.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para la petición de refresh de token.
 */
public record RefreshTokenRequest(

        @NotBlank(message = "El refresh token es obligatorio")
        String refreshToken
) {
}
