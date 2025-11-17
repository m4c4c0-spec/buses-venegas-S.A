package cl.venegas.buses_api.domain.repository;

import cl.venegas.buses_api.domain.model.entity.User;

import java.util.Optional;



public interface UserRepository {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    User save(User user);
    void delete(Long id);
}