package backend.src.main.java.cl.app.infrastructure.persistence.jpa.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.src.main.java.cl.app.infrastructure.persistence.jpa.entity.UserJpa;

public interface UserJpaRepository extends JpaRepository<UserJpa, Long> {
    Optional<UserJpa> findByEmail(String email);
}