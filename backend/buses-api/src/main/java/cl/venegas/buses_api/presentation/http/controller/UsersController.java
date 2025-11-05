package cl.venegas.buses_api.presentation.http.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.venegas.buses_api.presentation.http.request.RegisterUserRequest;
import cl.venegas.buses_api.presentation.http.response.UserResponse;
import jakarta.validation.Valid;
import cl.venegas.buses_api.application.usecase.RegisterUserService;

@RestController
@RequestMapping("/api/v1/users")
public class UsersController {
    private final RegisterUserService registerUser;

    public UsersController(RegisterUserService registerUser) {
        this.registerUser = registerUser;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        var user = registerUser.handle(
                request.email(),
                request.password(),
                request.firstName(),
                request.lastName(),
                request.phone()
        );

        return ResponseEntity.ok(UserResponse.from(user));
    }
}