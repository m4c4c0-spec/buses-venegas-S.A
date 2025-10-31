package cl.venegas.buses_api.infrastructure.security;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import cl.venegas.buses_api.presentation.http.request.HoldRequest;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@Configuration
public class SecurityConfig {
  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
    return http.build();
  }
  @PostMapping("/holds")
public ResponseEntity<?> hold(
  @Validated @RequestBody HoldRequest req, 
  @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = 1L; 
        var expires = service.handle(req.tripId(), req.seat(), userId);
    return ResponseEntity.ok(Map.of("expiresAt", expires.toString()));
}
}