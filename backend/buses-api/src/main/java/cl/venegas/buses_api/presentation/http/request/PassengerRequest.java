package cl.venegas.buses_api.presentation.http.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PassengerRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String documentType,
        @NotBlank String documentNumber,
        @Email String email,
        String phone
) {}