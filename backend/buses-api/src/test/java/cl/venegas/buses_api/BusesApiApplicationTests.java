package cl.venegas.buses_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * Test de integración para verificar que el contexto de Spring Boot carga correctamente.
 * 
 * Este test se mantiene igual en la versión Onion ya que solo verifica la carga
 * del contexto de la aplicación sin depender de la estructura interna de paquetes.
 * 
 * Configuración:
 * - Usa H2 en memoria para evitar dependencia de PostgreSQL
 * - Perfil "test" activo
 * - Flyway deshabilitado para tests
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.flyway.enabled=false",
    "spring.data.redis.host=localhost",
    "spring.data.redis.port=6379"
})
class BusesApiApplicationTests {

    @Test
    void contextLoads() {
        // Este test verifica que el contexto de Spring Boot
        // se carga correctamente sin errores de configuración
    }

}
