package cl.venegas.buses_api.application.usecase.trip;

import cl.venegas.buses_api.domain.model.entity.Trip;
import cl.venegas.buses_api.domain.repository.TripRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para SearchTripsUseCase
 * Adaptado de la versión PRE-migración (Hexagonal) a Onion Architecture
 * 
 * Cambios respecto a la versión anterior:
 * - Clase renombrada de SearchTripsService a SearchTripsUseCase
 * - Paquete cambió de application.usecase a application.usecase.trip
 * - Método renombrado de handle() a execute()
 * - Repositorio en domain.repository en vez de domain.port
 * - Trip en domain.model.entity
 * - Trip.basePrice() (Money) cambió a Trip.basePriceClp() (Integer)
 */
@ExtendWith(MockitoExtension.class)
class SearchTripsUseCaseTest {

        @Mock
        private TripRepository tripRepository;

        @InjectMocks
        private SearchTripsUseCase searchTripsUseCase;

        @Test
        @DisplayName("Debe encontrar viajes con los criterios especificados")
        void shouldSearchTripsSuccessfully() {
                // 1. ARRANGE
                String origin = "Santiago";
                String dest = "Temuco";
                LocalDate date = LocalDate.now();

                List<Trip> expectedTrips = List.of(
                                new Trip(1L, origin, dest,
                                                LocalDateTime.now(),
                                                LocalDateTime.now().plusHours(5),
                                                10000));

                when(tripRepository.findBy(origin, dest, date)).thenReturn(expectedTrips);

                // 2. ACT
                List<Trip> result = searchTripsUseCase.execute(origin, dest, date);

                // 3. ASSERT
                assertFalse(result.isEmpty());
                assertEquals(1, result.size());
                assertEquals(origin, result.get(0).origin());
                assertEquals(dest, result.get(0).destination());

                verify(tripRepository).findBy(origin, dest, date);
        }

        @Test
        @DisplayName("Debe retornar lista vacía cuando no hay viajes disponibles")
        void shouldReturnEmptyListWhenNoTripsFound() {
                // 1. ARRANGE
                String origin = "Santiago";
                String dest = "Punta Arenas";
                LocalDate date = LocalDate.now();

                when(tripRepository.findBy(origin, dest, date)).thenReturn(Collections.emptyList());

                // 2. ACT
                List<Trip> result = searchTripsUseCase.execute(origin, dest, date);

                // 3. ASSERT
                assertNotNull(result);
                assertTrue(result.isEmpty());

                verify(tripRepository).findBy(origin, dest, date);
        }

        @Test
        @DisplayName("Debe encontrar múltiples viajes para la misma ruta")
        void shouldFindMultipleTripsForSameRoute() {
                // 1. ARRANGE
                String origin = "Concepción";
                String dest = "Santiago";
                LocalDate date = LocalDate.now();

                List<Trip> expectedTrips = List.of(
                                new Trip(1L, origin, dest,
                                                LocalDateTime.now().withHour(8),
                                                LocalDateTime.now().withHour(14),
                                                12000),
                                new Trip(2L, origin, dest,
                                                LocalDateTime.now().withHour(14),
                                                LocalDateTime.now().withHour(20),
                                                12000),
                                new Trip(3L, origin, dest,
                                                LocalDateTime.now().withHour(22),
                                                LocalDateTime.now().plusDays(1).withHour(4),
                                                10000)); // Nocturno más barato

                when(tripRepository.findBy(origin, dest, date)).thenReturn(expectedTrips);

                // 2. ACT
                List<Trip> result = searchTripsUseCase.execute(origin, dest, date);

                // 3. ASSERT
                assertEquals(3, result.size());

                verify(tripRepository).findBy(origin, dest, date);
        }

        @Test
        @DisplayName("Debe buscar viajes con fecha futura")
        void shouldSearchTripsWithFutureDate() {
                // 1. ARRANGE
                String origin = "Temuco";
                String dest = "Valdivia";
                LocalDate futureDate = LocalDate.now().plusDays(7);

                List<Trip> expectedTrips = List.of(
                                new Trip(1L, origin, dest,
                                                futureDate.atTime(10, 0),
                                                futureDate.atTime(12, 30),
                                                8000));

                when(tripRepository.findBy(origin, dest, futureDate)).thenReturn(expectedTrips);

                // 2. ACT
                List<Trip> result = searchTripsUseCase.execute(origin, dest, futureDate);

                // 3. ASSERT
                assertFalse(result.isEmpty());

                verify(tripRepository).findBy(origin, dest, futureDate);
        }

        @Test
        @DisplayName("Debe pasar correctamente los parámetros al repositorio")
        void shouldPassCorrectParametersToRepository() {
                // 1. ARRANGE
                String origin = "Arica";
                String dest = "Iquique";
                LocalDate date = LocalDate.of(2025, 1, 15);

                when(tripRepository.findBy(origin, dest, date)).thenReturn(Collections.emptyList());

                // 2. ACT
                searchTripsUseCase.execute(origin, dest, date);

                // 3. ASSERT
                verify(tripRepository).findBy(origin, dest, date);
                verifyNoMoreInteractions(tripRepository);
        }

        @Test
        @DisplayName("Debe retornar viajes con precios diferentes")
        void shouldReturnTripsWithDifferentPrices() {
                // 1. ARRANGE
                String origin = "Santiago";
                String dest = "Viña del Mar";
                LocalDate date = LocalDate.now();

                List<Trip> expectedTrips = List.of(
                                new Trip(1L, origin, dest,
                                                LocalDateTime.now(),
                                                LocalDateTime.now().plusHours(2),
                                                5000), // Económico
                                new Trip(2L, origin, dest,
                                                LocalDateTime.now(),
                                                LocalDateTime.now().plusHours(2),
                                                8000), // Semi-cama
                                new Trip(3L, origin, dest,
                                                LocalDateTime.now(),
                                                LocalDateTime.now().plusHours(2),
                                                12000)); // Salón-cama

                when(tripRepository.findBy(origin, dest, date)).thenReturn(expectedTrips);

                // 2. ACT
                List<Trip> result = searchTripsUseCase.execute(origin, dest, date);

                // 3. ASSERT
                assertEquals(3, result.size());
                assertEquals(5000, result.get(0).basePriceClp());
                assertEquals(8000, result.get(1).basePriceClp());
                assertEquals(12000, result.get(2).basePriceClp());
        }

        @Test
        @DisplayName("Debe buscar viajes con origen y destino intercambiados")
        void shouldSearchTripsWithReversedOriginAndDestination() {
                // 1. ARRANGE
                String origin = "Temuco";
                String dest = "Santiago";
                LocalDate date = LocalDate.now();

                List<Trip> expectedTrips = List.of(
                                new Trip(1L, origin, dest,
                                                LocalDateTime.now(),
                                                LocalDateTime.now().plusHours(9),
                                                14990));

                when(tripRepository.findBy(origin, dest, date)).thenReturn(expectedTrips);

                // 2. ACT
                List<Trip> result = searchTripsUseCase.execute(origin, dest, date);

                // 3. ASSERT
                assertFalse(result.isEmpty());
                assertEquals(origin, result.get(0).origin());
                assertEquals(dest, result.get(0).destination());
        }
}
