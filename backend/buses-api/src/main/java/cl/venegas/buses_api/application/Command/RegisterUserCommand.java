package cl.venegas.buses_api.application.Command;

public record RegisterUserCommand(
    String email,
    String password,
    String firstName,
    String lastName,
    String phone
) {}