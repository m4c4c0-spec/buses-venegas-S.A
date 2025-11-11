package backend.src.main.java.cl.app.domain.port;

import java.util.Optional;
import backend.src.main.java.cl.app.domain.model.User;

public interface UserRepository {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    User save(User user);
    void delete(Long id);
}