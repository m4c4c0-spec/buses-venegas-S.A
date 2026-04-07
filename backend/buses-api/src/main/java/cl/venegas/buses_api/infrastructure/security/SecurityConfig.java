package cl.venegas.buses_api.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

/**
 * Configuracion de seguridad de Spring Security con JWT.
 *
 * Flujo de autenticacion:
 *   1. Cliente llama POST /api/v1/auth/login con email+password
 *   2. Backend valida credenciales y retorna accessToken (15min) + refreshToken (7 dias)
 *   3. Cliente incluye "Authorization: Bearer <accessToken>" en cada request protegido
 *   4. JwtAuthenticationFilter valida el token y establece el contexto de seguridad
 *   5. Cuando el accessToken expira, cliente llama POST /api/v1/auth/refresh con refreshToken
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .cors(org.springframework.security.config.Customizer.withDefaults())
        // CSRF deshabilitado: API REST stateless con JWT no usa cookies de sesion
        .csrf(csrf -> csrf.disable())
        // Politica de sesion STATELESS: Spring Security no crea ni usa HttpSession
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // Cabeceras de seguridad HTTP
        .headers(headers -> headers
            .xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
            .contentTypeOptions(contentType -> {})
            .frameOptions(frame -> frame.deny())
            .cacheControl(cache -> {})
        )
        // Reglas de autorizacion por endpoint
        .authorizeHttpRequests(auth -> auth
            // ===== ENDPOINTS COMPLETAMENTE PUBLICOS =====
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
            .requestMatchers("/actuator/health", "/actuator/info").permitAll()
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

            // Autenticacion: login, registro, refresh (siempre publicos)
            .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/v1/auth/refresh").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/v1/auth/logout").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/v1/users/register").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/v1/auth/register").permitAll()

            // Busqueda de viajes: publico (los usuarios buscan sin registrarse)
            .requestMatchers(HttpMethod.GET, "/api/v1/trips/**").permitAll()

            // ===== ENDPOINTS QUE REQUIEREN AUTENTICACION =====
            // Informacion del usuario autenticado
            .requestMatchers(HttpMethod.GET, "/api/v1/auth/me").authenticated()

            // Perfil de usuario: solo usuarios autenticados
            .requestMatchers(HttpMethod.GET, "/api/v1/users/**").authenticated()

            // Gestion de reservas: requiere usuario autenticado
            .requestMatchers("/api/v1/bookings/**").authenticated()

            // Retencion de asientos: requiere usuario autenticado
            .requestMatchers("/api/v1/seats/**").authenticated()

            // ===== ENDPOINTS CON ACCESO MIXTO =====
            // Reservas por email/rut (flujo legacy sin login)
            .requestMatchers("/api/reservas/**").permitAll()

            // Pagos con MercadoPago (webhook + checkout)
            .requestMatchers("/api/payments/**").permitAll()

            // Blockchain
            .requestMatchers("/api/blockchain/**").permitAll()

            // Cualquier otro endpoint no definido: DENEGAR
            .anyRequest().denyAll()
        )
        // Agregar el filtro JWT ANTES del filtro de autenticacion por usuario/password
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }
}
