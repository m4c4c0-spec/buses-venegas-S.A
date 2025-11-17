package cl.venegas.buses_api.domain.repository;

import cl.venegas.buses_api.domain.model.entity.Route;

import java.util.List;
import java.util.Optional;



public interface RouteRepository {
    Route save(Route route);
    Optional<Route> findById(Long id);
    List<Route> findByOriginAndDestination(String origin, String destination);
    List<Route> findActiveRoutes();
}