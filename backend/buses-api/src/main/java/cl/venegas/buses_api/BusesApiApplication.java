package cl.venegas.buses_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BusesApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BusesApiApplication.class, args);
	}

	@org.springframework.context.annotation.Bean
	public org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy cleanMigrateStrategy() {
		return flyway -> {
			try {
				flyway.migrate();
			} catch (org.flywaydb.core.api.FlywayException e) {
				flyway.clean();
				flyway.migrate();
			}
		};
	}

}
