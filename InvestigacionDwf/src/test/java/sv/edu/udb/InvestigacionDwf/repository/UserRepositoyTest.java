package sv.edu.udb.InvestigacionDwf.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import sv.edu.udb.InvestigacionDwf.model.entity.Role;
import sv.edu.udb.InvestigacionDwf.model.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private final String USERNAME = "juan123";
    private final String EMAIL = "juan@example.com";
    private final String PASSWORD = "pass123";
    private final String PRIMER_NOMBRE = "Juan";
    private final String PRIMER_APELLIDO = "Pérez";
    private final LocalDate FECHA_NACIMIENTO = LocalDate.of(1990, 5, 15);

    private Long savedUserId;
    private Role testRole;

    @BeforeEach
    void init() {
        testRole = roleRepository.save(new Role("ROLE_USER"));

        User user = User.builder()
                .username(USERNAME)
                .email(EMAIL)
                .password(PASSWORD)
                .primerNombre(PRIMER_NOMBRE)
                .primerApellido(PRIMER_APELLIDO)
                .fechaNacimiento(FECHA_NACIMIENTO)
                .role(testRole)
                .build();

        userRepository.save(user);
        savedUserId = user.getIdUser();
    }

    @AfterEach
    void clean() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void testFindById() {
        Optional<User> user = userRepository.findById(savedUserId);
        assertTrue(user.isPresent());
        assertEquals(USERNAME, user.get().getUsername());
    }

    @Test
    void testFindAll() {
        List<User> users = userRepository.findAll();
        assertNotNull(users);
        assertFalse(users.isEmpty());
    }

    @Test
    void testSave() {
        User user = User.builder()
                .username("maria123")
                .email("maria@example.com")
                .password("pass456")
                .primerNombre("Maria")
                .primerApellido("Gonzalez")
                .fechaNacimiento(LocalDate.of(1995, 8, 22))
                .role(testRole)
                .build();
        User saved = userRepository.save(user);
        assertNotNull(saved.getIdUser());
        assertEquals("maria123", saved.getUsername());
    }

    @Test
    void testUpdate() {
        Optional<User> userOpt = userRepository.findById(savedUserId);
        assertTrue(userOpt.isPresent());
        User user = userOpt.get();
        user.setPrimerNombre("Juanito");
        userRepository.save(user);

        Optional<User> updated = userRepository.findById(savedUserId);
        assertTrue(updated.isPresent());
        assertEquals("Juanito", updated.get().getPrimerNombre());
    }

    @Test
    void testDelete() {
        Optional<User> userOpt = userRepository.findById(savedUserId);
        assertTrue(userOpt.isPresent());
        userRepository.delete(userOpt.get());
        Optional<User> deleted = userRepository.findById(savedUserId);
        assertFalse(deleted.isPresent());
    }

    @Test
    void shouldReturnUserByUsername_When_UsernameExists() {
        Optional<User> user = userRepository.findByUsername(USERNAME);

        assertTrue(user.isPresent());
        assertEquals(USERNAME, user.get().getUsername());
        assertEquals(EMAIL, user.get().getEmail());
    }

    @Test
    void shouldCheckIfUsernameExists_ReturnsTrue() {
        boolean exists = userRepository.existsByUsername(USERNAME);
        assertTrue(exists);
    }

    @Test
    void shouldCheckIfEmailExists_ReturnsTrue() {
        boolean exists = userRepository.existsByEmail(EMAIL);
        assertTrue(exists);
    }

    @Test
    void shouldCheckIfNonExistentUser_ReturnsFalse() {
        boolean existsByUsername = userRepository.existsByUsername("nonexistentuser");
        boolean existsByEmail = userRepository.existsByEmail("nonexistent@example.com");

        assertFalse(existsByUsername);
        assertFalse(existsByEmail);
    }

    // ...puedes mantener los otros tests adicionales si lo deseas...
}