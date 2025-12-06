package cl.venegas.buses_api.interfaces.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.venegas.buses_api.application.usecase.user.RegisterUserService;
import cl.venegas.buses_api.domain.model.entity.User;
import cl.venegas.buses_api.interfaces.web.dto.request.RegisterUserRequest;
import cl.venegas.buses_api.interfaces.web.dto.response.UserResponse;
import cl.venegas.buses_api.interfaces.web.mapper.UserDTOMapper;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")

public class UsersController {

    private final RegisterUserService registerUserUseCase;
    private final UserDTOMapper mapper;

    public UsersController(
            RegisterUserService registerUserUseCase,
            UserDTOMapper mapper) {
        this.registerUserUseCase = registerUserUseCase;
        this.mapper = mapper;
    }

    /**
     * POST /api/v1/users/register
     * Registra el usuario
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(
            @RequestBody @Valid RegisterUserRequest request) {

        // Mapear request DTO a comando de dominio
        var command = mapper.toRegisterCommand(request);

        // Ejecutar caso de uso
        User user = registerUserUseCase.execute(command);

        // Mapear entidad de dominio a response DTO
        UserResponse response = mapper.toResponse(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/v1/users/{userId}
     * Se obtiene el usuario mediante el id
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable String userId) {

        return ResponseEntity.status(501).build();
    }
}
