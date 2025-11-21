package cl.venegas.buses_api.interfaces.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record PassengerRequest(

                @NotBlank(message = "Nombre es requerido") String firstName,

                @NotBlank(message = "Apellido es requerido") String lastName,

                @NotBlank(message = "Numero de documento es requerido") String documentNumber,

                @NotBlank(message = "Tipo de documento es requerido") String documentType,

                @Email(message = "Formato de correo invalido") String email,

                @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "Formato del numero de telefono invalido") String phone) {
}