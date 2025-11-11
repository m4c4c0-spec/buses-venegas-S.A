package backend.src.main.java.cl.app.domain.port;

import java.util.List;
import java.util.Optional;
import backend.src.main.java.cl.app.domain.model.Route;

public interface RouteRepository {
    Route save(Route route);
    Optional<Route> findById(Long id);
    List<Route> findByOriginAndDestination(String origin, String destination);
    List<Route> findActiveRoutes();
}