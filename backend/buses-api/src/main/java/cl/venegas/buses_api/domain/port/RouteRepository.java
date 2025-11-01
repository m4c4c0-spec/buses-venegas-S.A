package cl.venegas.buses_api.domain.port;

import java.util.List;
import java.util.Optional;
import cl.venegas.buses_api.domain.model.Route;

public interface RouteRepository {
    Route save(Route route);
    Optional<Route> findById(Long id);
    List<Route> findByOriginAndDestination(String origin, String destination);
    List<Route> findActiveRoutes();
}