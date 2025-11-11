package backend.src.main.java.cl.app.presentation.http.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.src.main.java.cl.app.presentation.http.request.RegisterUserRequest;
import backend.src.main.java.cl.app.presentation.http.response.UserResponse;
import jakarta.validation.Valid;
import backend.src.main.java.cl.app.application.usecase.RegisterUserService;

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