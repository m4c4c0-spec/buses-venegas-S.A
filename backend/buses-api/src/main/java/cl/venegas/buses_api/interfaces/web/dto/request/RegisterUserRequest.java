package cl.venegas.buses_api.interfaces.web.dto.request;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email; 

public record RegisterUserRequest(
        
        @NotBlank(message = "El correo es requerido")
        @Email(message = "el formato del email es invalido")
        String email,
        
        @NotBlank(message = "La contrasena es requerida")
        @Size(min = 8, message = "La contrasena debe ser de 8 caracteres")
        String password,
        
        @NotBlank(message = "El  nombre es requerido")
        String firstName,
        
        @NotBlank(message = "El apellido es requerido")
        String lastName,
        
        @NotBlank(message = "el telefono es requerido")
        String phone
) {}