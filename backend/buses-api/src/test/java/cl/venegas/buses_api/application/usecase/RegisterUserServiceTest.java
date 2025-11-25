package cl.venegas.buses_api.application.usecase;

import cl.venegas.buses_api.domain.model.User;
import cl.venegas.buses_api.domain.port.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterUserService registerUserService;

    @Test
    @DisplayName("Debe registrar un usuario correctamente")
    void shouldRegisterUserSuccessfully() {
        // ARRANGE
        String email = "test@example.com";
        String password = "password123";
        String encodedPassword = "encodedPassword123";
        String firstName = "John";
        String lastName = "Doe";
        String phone = "123456789";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        // ACT
        User result = registerUserService.handle(email, password, firstName, lastName, phone);

        // ASSERT
        assertNotNull(result);
        assertEquals(email, result.email().getValue());
        assertEquals(firstName, result.firstName());
        assertEquals(lastName, result.lastName());

        // Verificamos que se llamó a save
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode(password);
    }

    @Test
    @DisplayName("Debe lanzar excepción si el email ya existe")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // ARRANGE
        String email = "existing@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mock(User.class)));

        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class,
                () -> registerUserService.handle(email, "pass", "John", "Doe", "123456789"));
    }
}
