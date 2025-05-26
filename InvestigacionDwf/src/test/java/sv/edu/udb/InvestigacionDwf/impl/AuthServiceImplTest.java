package sv.edu.udb.InvestigacionDwf.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import sv.edu.udb.InvestigacionDwf.dto.request.LoginRequest;
import sv.edu.udb.InvestigacionDwf.dto.request.RegisterRequest;
import sv.edu.udb.InvestigacionDwf.exception.BadCredentialsException;
import sv.edu.udb.InvestigacionDwf.exception.ResourceNotFoundException;
import sv.edu.udb.InvestigacionDwf.exception.UserAlreadyExistException;
import sv.edu.udb.InvestigacionDwf.model.entity.Role;
import sv.edu.udb.InvestigacionDwf.model.entity.User;
import sv.edu.udb.InvestigacionDwf.repository.RoleRepository;
import sv.edu.udb.InvestigacionDwf.repository.UserRepository;
import sv.edu.udb.InvestigacionDwf.security.jwt.JwtUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtils jwtUtils;

    @Test
    void register_successful() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("testuser");
        req.setPassword("password123");
        req.setEmail("test@example.com");
        req.setPrimerNombre("Test");
        req.setPrimerApellido("User");
        req.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        req.setDui("12345678-9");
        req.setTelefono("1234-5678");
        req.setDireccion("Calle 1");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        Role role = new Role("ROLE_USER");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("password123")).thenReturn("encodedpass");
        when(jwtUtils.generateToken(anyString(), any(), anyString())).thenReturn("jwt-token");

        String token = authService.register(req);

        assertEquals("jwt-token", token);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_throwsIfUsernameExists() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("testuser");
        req.setPassword("password123");
        req.setEmail("test@example.com");
        req.setPrimerNombre("Test");
        req.setPrimerApellido("User");
        req.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        req.setDui("12345678-9");
        req.setTelefono("1234-5678");
        req.setDireccion("Calle 1");

        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThrows(UserAlreadyExistException.class, () -> authService.register(req));
    }

    @Test
    void register_throwsIfEmailExists() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("testuser");
        req.setPassword("password123");
        req.setEmail("test@example.com");
        req.setPrimerNombre("Test");
        req.setPrimerApellido("User");
        req.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        req.setDui("12345678-9");
        req.setTelefono("1234-5678");
        req.setDireccion("Calle 1");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(UserAlreadyExistException.class, () -> authService.register(req));
    }

    @Test
    void register_throwsIfRoleNotFound() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("testuser");
        req.setPassword("password123");
        req.setEmail("test@example.com");
        req.setPrimerNombre("Test");
        req.setPrimerApellido("User");
        req.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        req.setDui("12345678-9");
        req.setTelefono("1234-5678");
        req.setDireccion("Calle 1");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.register(req));
    }

    @Test
    void login_successful() {
        LoginRequest req = new LoginRequest();
        req.setUsername("testuser");
        req.setPassword("password123");

        Role role = new Role("ROLE_USER");
        User user = User.builder()
                .username("testuser")
                .password("encodedpass")
                .role(role)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedpass")).thenReturn(true);
        when(jwtUtils.generateToken(anyString(), any(), anyString())).thenReturn("jwt-token");

        String token = authService.login(req);

        assertEquals("jwt-token", token);
    }

    @Test
    void login_throwsIfUserNotFound() {
        LoginRequest req = new LoginRequest();
        req.setUsername("testuser");
        req.setPassword("password123");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authService.login(req));
    }

    @Test
    void login_throwsIfPasswordIncorrect() {
        LoginRequest req = new LoginRequest();
        req.setUsername("testuser");
        req.setPassword("wrongpass");

        Role role = new Role("ROLE_USER");
        User user = User.builder()
                .username("testuser")
                .password("encodedpass")
                .role(role)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "encodedpass")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(req));
    }

    @Test
    void register_throwsIfRequestIsNull() {
        assertThrows(IllegalArgumentException.class, () -> authService.register(null));
    }

    @Test
    void register_throwsIfUnderage() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("testuser");
        req.setPassword("password123");
        req.setEmail("test@example.com");
        req.setPrimerNombre("Test");
        req.setPrimerApellido("User");
        req.setFechaNacimiento(LocalDate.now().minusYears(17));
        req.setDui("12345678-9");
        req.setTelefono("1234-5678");
        req.setDireccion("Calle 1");

        assertThrows(IllegalArgumentException.class, () -> authService.register(req));
    }

    @Test
    void login_throwsIfRequestIsNull() {
        assertThrows(IllegalArgumentException.class, () -> authService.login(null));
    }
}
