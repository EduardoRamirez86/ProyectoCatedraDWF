package sv.edu.udb.InvestigacionDwf.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import sv.edu.udb.InvestigacionDwf.model.entity.Cupon;
import sv.edu.udb.InvestigacionDwf.model.entity.User;
import sv.edu.udb.InvestigacionDwf.model.entity.Role;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CuponRepositoryTest {

    @Autowired
    private CuponRepository cuponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Long savedCuponId;
    private User user;

    @BeforeEach
    void init() {
        Role role = roleRepository.save(new Role("ROLE_USER"));
        user = userRepository.save(User.builder().username("cuponuser").email("cu@cu.com").password("pass").primerNombre("a").primerApellido("b").role(role).build());
        Cupon cupon = Cupon.builder()
                .user(user)
                .codigo("CUPON123")
                .porcentajeDescuento(10.0)
                .usado(false)
                .fechaCreacion(LocalDateTime.now())
                .fechaExpiracion(LocalDateTime.now().plusDays(1))
                .build();
        cuponRepository.save(cupon);
        savedCuponId = cupon.getIdCupon();
    }

    @AfterEach
    void clean() {
        cuponRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void testFindById() {
        Optional<Cupon> cupon = cuponRepository.findById(savedCuponId);
        assertTrue(cupon.isPresent());
        assertEquals("CUPON123", cupon.get().getCodigo());
    }

    @Test
    void testFindAll() {
        List<Cupon> cupones = cuponRepository.findAll();
        assertNotNull(cupones);
        assertFalse(cupones.isEmpty());
    }

    @Test
    void testSave() {
        Cupon cupon = Cupon.builder()
                .user(user)
                .codigo("CUPON456")
                .porcentajeDescuento(20.0)
                .usado(false)
                .fechaCreacion(LocalDateTime.now())
                .fechaExpiracion(LocalDateTime.now().plusDays(2))
                .build();
        Cupon saved = cuponRepository.save(cupon);
        assertNotNull(saved.getIdCupon());
        assertEquals("CUPON456", saved.getCodigo());
    }

    @Test
    void testUpdate() {
        Optional<Cupon> cuponOpt = cuponRepository.findById(savedCuponId);
        assertTrue(cuponOpt.isPresent());
        Cupon cupon = cuponOpt.get();
        cupon.setPorcentajeDescuento(15.0);
        cuponRepository.save(cupon);

        Optional<Cupon> updated = cuponRepository.findById(savedCuponId);
        assertTrue(updated.isPresent());
        assertEquals(15.0, updated.get().getPorcentajeDescuento());
    }

    @Test
    void testDelete() {
        Optional<Cupon> cuponOpt = cuponRepository.findById(savedCuponId);
        assertTrue(cuponOpt.isPresent());
        cuponRepository.delete(cuponOpt.get());
        Optional<Cupon> deleted = cuponRepository.findById(savedCuponId);
        assertFalse(deleted.isPresent());
    }
}
