package sv.edu.udb.InvestigacionDwf.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import sv.edu.udb.InvestigacionDwf.model.entity.Direccion;
import sv.edu.udb.InvestigacionDwf.model.entity.User;
import sv.edu.udb.InvestigacionDwf.model.entity.Role;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class DireccionRepositoryTest {

    @Autowired
    private DireccionRepository direccionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Long savedDireccionId;
    private User user;

    @BeforeEach
    void init() {
        Role role = roleRepository.save(new Role("ROLE_USER"));
        user = userRepository.save(User.builder().username("diruser").email("dir@dir.com").password("pass").primerNombre("a").primerApellido("b").role(role).build());
        Direccion direccion = Direccion.builder().user(user).alias("Casa").build();
        direccionRepository.save(direccion);
        savedDireccionId = direccion.getIdDireccion();
    }

    @AfterEach
    void clean() {
        direccionRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void testFindById() {
        Optional<Direccion> direccion = direccionRepository.findById(savedDireccionId);
        assertTrue(direccion.isPresent());
        assertEquals(user.getIdUser(), direccion.get().getUser().getIdUser());
    }

    @Test
    void testFindAll() {
        List<Direccion> direcciones = direccionRepository.findAll();
        assertNotNull(direcciones);
        assertFalse(direcciones.isEmpty());
    }

    @Test
    void testSave() {
        Direccion direccion = Direccion.builder().user(user).alias("Trabajo").build();
        Direccion saved = direccionRepository.save(direccion);
        assertNotNull(saved.getIdDireccion());
        assertEquals(user.getIdUser(), saved.getUser().getIdUser());
    }

    @Test
    void testUpdate() {
        Optional<Direccion> direccionOpt = direccionRepository.findById(savedDireccionId);
        assertTrue(direccionOpt.isPresent());
        Direccion direccion = direccionOpt.get();
        direccion.setAlias("Nueva Casa");
        direccionRepository.save(direccion);

        Optional<Direccion> updated = direccionRepository.findById(savedDireccionId);
        assertTrue(updated.isPresent());
        assertEquals("Nueva Casa", updated.get().getAlias());
    }

    @Test
    void testDelete() {
        Optional<Direccion> direccionOpt = direccionRepository.findById(savedDireccionId);
        assertTrue(direccionOpt.isPresent());
        direccionRepository.delete(direccionOpt.get());
        Optional<Direccion> deleted = direccionRepository.findById(savedDireccionId);
        assertFalse(deleted.isPresent());
    }

    @Test
    void shouldSaveAndFindByUserIdUser() {
        Role role = roleRepository.findByName("ROLE_USER").orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
        User user = userRepository.save(User.builder()
            .username("diruser2")
            .email("dir2@dir.com")
            .password("pass")
            .primerNombre("a")
            .primerApellido("b")
            .role(role)
            .build());
        Direccion direccion = Direccion.builder().user(user).alias("Casa").build();
        direccionRepository.save(direccion);

        List<Direccion> found = direccionRepository.findByUser_IdUser(user.getIdUser());
        assertFalse(found.isEmpty());
        assertEquals(user.getIdUser(), found.get(0).getUser().getIdUser());
    }
}
