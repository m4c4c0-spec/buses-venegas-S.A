package cl.venegas.buses_api.infrastructure.config;

import cl.venegas.buses_api.infrastructure.persistence.jpa.entity.TripJpa;
import cl.venegas.buses_api.infrastructure.persistence.jpa.repo.TripJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Configuration
public class TripSeeder {

    @Bean
    public CommandLineRunner loadTrips(TripJpaRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                // Generar viajes entre las capitales y puntos fuertes para pruebas
                LocalDate today = LocalDate.now();
                List<String> ciudades = Arrays.asList(
                    "Ancud (Terminal Municipal)", "Puerto Montt (Terminal de Buses)", 
                    "Puerto Varas", "Osorno", "Valdivia (Terminal)", 
                    "Temuco (Terminal Araucanía)", "Los Ángeles", 
                    "Chillán (Terminal María Teresa)", "Talca", "Rancagua", 
                    "Santiago (Terminal Sur / Estación Central)"
                );

                // Permutaciones de la ruta 5 Sur
                for (int i = 0; i < ciudades.size() - 1; i++) {
                    for (int j = i + 1; j < ciudades.size(); j++) {
                        String origen = ciudades.get(i);
                        String destino = ciudades.get(j);
                        
                        // Viajes para hoy
                        crearViaje(repository, origen, destino, today.atTime(8, 0), j - i);
                        crearViaje(repository, origen, destino, today.atTime(14, 30), j - i);
                        crearViaje(repository, origen, destino, today.atTime(21, 0), j - i);
                        
                        // Viajes para mañana
                        crearViaje(repository, origen, destino, today.plusDays(1).atTime(9, 15), j - i);
                        crearViaje(repository, origen, destino, today.plusDays(1).atTime(16, 45), j - i);
                        
                        // Sentido opuesto (Norte a Sur)
                        crearViaje(repository, destino, origen, today.atTime(10, 0), j - i);
                        crearViaje(repository, destino, origen, today.atTime(18, 0), j - i);
                        crearViaje(repository, destino, origen, today.plusDays(1).atTime(11, 30), j - i);
                    }
                }
            }
        };
    }

    private void crearViaje(TripJpaRepository repo, String origen, String destino, LocalDateTime salida, int saltos) {
        TripJpa trip = new TripJpa();
        trip.setOrigin(origen);
        trip.setDestination(destino);
        trip.setDepartureTs(salida);
        // Calcula llegada sumando horas según la distancia (saltos en el index)
        trip.setArrivalTs(salida.plusHours(saltos).plusMinutes(30)); 
        // Calcula precio según cantidad de saltos
        trip.setBasePriceClp(8000 + (saltos * 3500)); 
        repo.save(trip);
    }
}
