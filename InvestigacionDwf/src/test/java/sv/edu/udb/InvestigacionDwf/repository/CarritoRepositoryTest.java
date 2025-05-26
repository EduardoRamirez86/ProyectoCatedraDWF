package sv.edu.udb.InvestigacionDwf.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import sv.edu.udb.InvestigacionDwf.model.entity.Carrito;
import sv.edu.udb.InvestigacionDwf.model.entity.User;
import sv.edu.udb.InvestigacionDwf.model.entity.Role;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CarritoRepositoryTest {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Long savedCarritoId;
    private User user;

    @BeforeEach
    void init() {
        Role role = roleRepository.findByName("ROLE_USER").orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
        user = userRepository.save(User.builder().username("carritouser").email("c@c.com").password("pass").primerNombre("a").primerApellido("b").role(role).build());
        Carrito carrito = Carrito.builder().user(user).fechaCreacion(LocalDateTime.now()).build();
        carritoRepository.save(carrito);
        savedCarritoId = carrito.getIdCarrito();
    }

    @AfterEach
    void clean() {
        carritoRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindByUserIdUser() {
        Role role = roleRepository.findByName("ROLE_USER").orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
        User user = userRepository.save(User.builder().username("carritouser2").email("c2@c.com").password("pass").primerNombre("a").primerApellido("b").role(role).build());
        Carrito carrito = Carrito.builder().user(user).fechaCreacion(LocalDateTime.now()).build();
        carritoRepository.save(carrito);

        Optional<Carrito> found = carritoRepository.findByUserIdUser(user.getIdUser());
        assertTrue(found.isPresent());
        assertEquals(user.getIdUser(), found.get().getUser().getIdUser());
    }

    @Test
    void testFindById() {
        Optional<Carrito> carrito = carritoRepository.findById(savedCarritoId);
        assertTrue(carrito.isPresent());
        assertEquals(user.getIdUser(), carrito.get().getUser().getIdUser());
    }

    @Test
    void testFindAll() {
        List<Carrito> carritos = carritoRepository.findAll();
        assertNotNull(carritos);
        assertFalse(carritos.isEmpty());
    }

    @Test
    void testSave() {
        // Crea un usuario diferente para evitar duplicidad en id_user (único en carrito)
        Role role = roleRepository.findByName("ROLE_USER").orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
        User anotherUser = userRepository.save(User.builder()
                .username("carritouser3")
                .email("c3@c.com")
                .password("pass")
                .primerNombre("a")
                .primerApellido("b")
                .role(role)
                .build());
        Carrito carrito = Carrito.builder().user(anotherUser).fechaCreacion(LocalDateTime.now()).build();
        Carrito saved = carritoRepository.save(carrito);
        assertNotNull(saved.getIdCarrito());
        assertEquals(anotherUser.getIdUser(), saved.getUser().getIdUser());
    }

    @Test
    void testUpdate() {
        Optional<Carrito> carritoOpt = carritoRepository.findById(savedCarritoId);
        assertTrue(carritoOpt.isPresent());
        Carrito carrito = carritoOpt.get();
        LocalDateTime nuevaFecha = LocalDateTime.now().plusDays(1);
        carrito.setFechaCreacion(nuevaFecha);
        carritoRepository.save(carrito);

        Optional<Carrito> updated = carritoRepository.findById(savedCarritoId);
        assertTrue(updated.isPresent());
        assertEquals(nuevaFecha, updated.get().getFechaCreacion());
    }

    @Test
    void testDelete() {
        Optional<Carrito> carritoOpt = carritoRepository.findById(savedCarritoId);
        assertTrue(carritoOpt.isPresent());
        carritoRepository.delete(carritoOpt.get());
        Optional<Carrito> deleted = carritoRepository.findById(savedCarritoId);
        assertFalse(deleted.isPresent());
    }
}
