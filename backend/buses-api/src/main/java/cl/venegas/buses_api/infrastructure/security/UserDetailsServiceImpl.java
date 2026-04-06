package cl.venegas.buses_api.infrastructure.security;

import cl.venegas.buses_api.infrastructure.persistence.jpa.entity.UserJpa;
import cl.venegas.buses_api.infrastructure.persistence.jpa.repo.UserJpaRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación de UserDetailsService para Spring Security.
 * Carga el usuario desde la base de datos por email para el proceso de login.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserJpaRepository userJpaRepository;

    public UserDetailsServiceImpl(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserJpa userJpa = userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con email: " + email));

        return new org.springframework.security.core.userdetails.User(
                userJpa.getEmail(),
                userJpa.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + userJpa.getRole().name()))
        );
    }
}
