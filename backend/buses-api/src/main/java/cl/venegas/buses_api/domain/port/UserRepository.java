package cl.venegas.buses_api.domain.port;

import java.util.Optional;
import cl.venegas.buses_api.domain.model.User;

public interface UserRepository {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    User save(User user);
    void delete(Long id);
}