package cl.venegas.buses_api.interfaces.web.controller;

import cl.venegas.buses_api.infrastructure.persistence.jpa.entity.UserJpa;
import cl.venegas.buses_api.infrastructure.persistence.jpa.repo.UserJpaRepository;
import cl.venegas.buses_api.infrastructure.security.JwtTokenProvider;
import cl.venegas.buses_api.interfaces.web.dto.request.LoginRequest;
import cl.venegas.buses_api.interfaces.web.dto.request.RefreshTokenRequest;
import cl.venegas.buses_api.interfaces.web.dto.response.AuthResponse;
import cl.venegas.buses_api.interfaces.web.dto.response.UserResponse;
import cl.venegas.buses_api.interfaces.web.mapper.UserDTOMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * Controlador de autenticación.
 * Maneja login, refresh de tokens y logout.
 *
 * Endpoints públicos (sin token):
 *   POST /api/v1/auth/login    → Retorna access + refresh token
 *   POST /api/v1/auth/refresh  → Renueva el access token
 *   POST /api/v1/auth/logout   → Invalida la sesión (client-side)
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    // Expiración access token en segundos (15 min)
    private static final long ACCESS_TOKEN_EXPIRY_SEC = 15 * 60;

    private final JwtTokenProvider tokenProvider;
    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDTOMapper userMapper;

    public AuthController(JwtTokenProvider tokenProvider,
                          UserJpaRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          UserDTOMapper userMapper) {
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    /**
     * POST /api/v1/auth/login
     * Autentica al usuario con email y contraseña.
     * Retorna access token (15 min) y refresh token (7 días).
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        log.info("Intento de login para email: {}", request.email());

        Optional<UserJpa> userOpt = userRepository.findByEmail(request.email());

        if (userOpt.isEmpty()) {
            log.warn("Login fallido - usuario no encontrado: {}", request.email());
            // Mensaje genérico para no revelar si el email existe
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas."));
        }

        UserJpa userJpa = userOpt.get();

        if (!passwordEncoder.matches(request.password(), userJpa.getPasswordHash())) {
            log.warn("Login fallido - contraseña incorrecta para: {}", request.email());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas."));
        }

        String role = userJpa.getRole().name();
        String accessToken = tokenProvider.generateAccessToken(userJpa.getEmail(), role);
        String refreshToken = tokenProvider.generateRefreshToken(userJpa.getEmail());

        UserResponse userResponse = userMapper.toResponse(userJpa.toDomain());

        log.info("Login exitoso para usuario: {}", userJpa.getEmail());

        return ResponseEntity.ok(AuthResponse.of(
                accessToken,
                refreshToken,
                ACCESS_TOKEN_EXPIRY_SEC,
                userResponse
        ));
    }

    /**
     * POST /api/v1/auth/refresh
     * Renueva el access token usando un refresh token válido.
     * El refresh token NO se rota (se mantiene el mismo hasta que expire).
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();

        if (!tokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Refresh token inválido o expirado. Inicia sesión nuevamente."));
        }

        if (!tokenProvider.isRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "El token proporcionado no es un refresh token."));
        }

        String email = tokenProvider.getEmailFromToken(refreshToken);

        // Verificar que el usuario aún existe en la base de datos
        Optional<UserJpa> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Usuario no encontrado."));
        }

        String role = userOpt.get().getRole().name();
        String newAccessToken = tokenProvider.generateAccessToken(email, role);

        log.info("Access token renovado para usuario: {}", email);

        return ResponseEntity.ok(Map.of(
                "accessToken", newAccessToken,
                "tokenType", "Bearer",
                "expiresIn", ACCESS_TOKEN_EXPIRY_SEC
        ));
    }

    /**
     * POST /api/v1/auth/logout
     * El logout en JWT es del lado del cliente (eliminar tokens del storage).
     * Este endpoint existe para completar el flujo y puede usarse para
     * registrar el evento de logout.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // En una implementación con blacklist de tokens, aquí se invalidaría el token.
        // Por ahora el cliente debe eliminar los tokens de su storage local.
        log.info("Logout solicitado");
        return ResponseEntity.ok(Map.of("message", "Sesión cerrada correctamente."));
    }

    /**
     * GET /api/v1/auth/me
     * Retorna información del usuario autenticado (requiere access token válido).
     */
    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token requerido."));
        }

        String token = authHeader.substring(7);
        if (!tokenProvider.validateToken(token) || !tokenProvider.isAccessToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido."));
        }

        String email = tokenProvider.getEmailFromToken(token);
        Optional<UserJpa> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado."));
        }

        return ResponseEntity.ok(userMapper.toResponse(userOpt.get().toDomain()));
    }
}
