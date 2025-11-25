package cl.venegas.buses_api.infrastructure.persistence.jpa.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cl.venegas.buses_api.domain.model.User;
import cl.venegas.buses_api.domain.model.UserRole;
import cl.venegas.buses_api.domain.model.valueobject.Email;
import cl.venegas.buses_api.domain.model.valueobject.Password;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public User toDomain() {
        return new User(
                id,
                new Email(email),
                new Password(passwordHash),
                firstName,
                lastName,
                phone,
                role,
                createdAt);
    }

    public static UserJpa fromDomain(User user, String passwordHash) {
        return new UserJpa(
                user.id(),
                user.email().getValue(),
                passwordHash,
                user.firstName(),
                user.lastName(),
                user.phone(),
                user.role(),
                user.createdAt(),
                LocalDateTime.now());
    }
}