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

}
