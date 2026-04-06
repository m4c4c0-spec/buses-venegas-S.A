package cl.venegas.buses_api.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

  @Value("${CORS_ALLOWED_ORIGINS:http://localhost:3000,http://localhost:5173,http://localhost:5174}")
  private String allowedOrigins;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    // SEGURIDAD: Usar origenes especificos en lugar de wildcard "*"
    // Nunca usar allowedOriginPatterns("*") con allowCredentials(true)
    String[] origins = allowedOrigins.split(",");
    registry.addMapping("/**")
        .allowedOrigins(origins)
        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        .allowedHeaders("Content-Type", "Authorization", "X-Requested-With")
        .exposedHeaders("X-Total-Count")
        .allowCredentials(true)
        .maxAge(3600); // Cache preflight por 1 hora
  }
}
