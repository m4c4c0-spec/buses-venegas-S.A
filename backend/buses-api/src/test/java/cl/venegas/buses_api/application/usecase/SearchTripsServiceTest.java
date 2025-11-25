package cl.venegas.buses_api.application.usecase;

import cl.venegas.buses_api.domain.model.Trip;
import cl.venegas.buses_api.domain.model.valueobject.Money;
import cl.venegas.buses_api.domain.port.TripRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchTripsServiceTest {
    @Mock
    private TripRepository tripRepository;
    @InjectMocks
    private SearchTripsService searchTripsService;

    @Test
    @DisplayName("Debe encontrar viajes con los criterios especificados")
    void shouldSearchTripsSuccessfully() {
        // 1. ARRANGE
        String origin = "Santiago";
        String dest = "Temuco";
        LocalDate date = LocalDate.now();
        List<Trip> expectedTrips = List.of(new Trip(1L, origin, dest, java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now().plusHours(5), Money.of(10000)));
        when(tripRepository.findBy(origin, dest, date)).thenReturn(expectedTrips);
        // 2. ACT
        List<Trip> result = searchTripsService.handle(origin, dest, date);
        // 3. ASSERT
        assertFalse(result.isEmpty());
        verify(tripRepository).findBy(origin, dest, date);
    }
}
