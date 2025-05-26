package sv.edu.udb.InvestigacionDwf.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import sv.edu.udb.InvestigacionDwf.model.entity.Resena;
import sv.edu.udb.InvestigacionDwf.model.entity.Producto;
import sv.edu.udb.InvestigacionDwf.model.entity.User;
import sv.edu.udb.InvestigacionDwf.model.entity.TipoProducto;
import sv.edu.udb.InvestigacionDwf.model.entity.Role;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ResenaRepositoryTest {

    @Autowired
    private ResenaRepository resenaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private TipoProductoRepository tipoProductoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Long savedResenaId;
    private Producto producto;
    private User user;

    @BeforeEach
    void init() {
        TipoProducto tipoProducto = tipoProductoRepository.save(TipoProducto.builder().tipo("General").descripcion("desc").build());
        producto = productoRepository.save(Producto.builder()
                .nombre("Prod1")
                .cantidad(1)
                .cantidadPuntos(1)
                .precio(BigDecimal.ONE)
                .costo(BigDecimal.ONE)
                .tipoProducto(tipoProducto)
                .build());
        Role role = roleRepository.save(new Role("ROLE_USER"));
        user = userRepository.save(User.builder()
                .username("user1")
                .email("user1@mail.com")
                .password("pass")
                .primerNombre("a")
                .primerApellido("b")
                .role(role)
                .build());
        Resena resena = Resena.builder()
                .producto(producto)
                .user(user)
                .comentario("comentario")
                .build();
        resenaRepository.save(resena);
        savedResenaId = resena.getIdResena();
    }

    @AfterEach
    void clean() {
        resenaRepository.deleteAll();
        productoRepository.deleteAll();
        tipoProductoRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void testFindById() {
        Optional<Resena> resena = resenaRepository.findById(savedResenaId);
        assertTrue(resena.isPresent());
        assertEquals("comentario", resena.get().getComentario());
    }

    @Test
    void testFindAll() {
        List<Resena> resenas = resenaRepository.findAll();
        assertNotNull(resenas);
        assertFalse(resenas.isEmpty());
    }

    @Test
    void testSave() {
        Resena resena = Resena.builder()
                .producto(producto)
                .user(user)
                .comentario("nuevo comentario")
                .build();
        Resena saved = resenaRepository.save(resena);
        assertNotNull(saved.getIdResena());
        assertEquals("nuevo comentario", saved.getComentario());
    }

    @Test
    void testUpdate() {
        Optional<Resena> resenaOpt = resenaRepository.findById(savedResenaId);
        assertTrue(resenaOpt.isPresent());
        Resena resena = resenaOpt.get();
        resena.setComentario("comentario actualizado");
        resenaRepository.save(resena);

        Optional<Resena> updated = resenaRepository.findById(savedResenaId);
        assertTrue(updated.isPresent());
        assertEquals("comentario actualizado", updated.get().getComentario());
    }

    @Test
    void testDelete() {
        Optional<Resena> resenaOpt = resenaRepository.findById(savedResenaId);
        assertTrue(resenaOpt.isPresent());
        resenaRepository.delete(resenaOpt.get());
        Optional<Resena> deleted = resenaRepository.findById(savedResenaId);
        assertFalse(deleted.isPresent());
    }

    @Test
    void shouldSaveAndFindByProductoIdProducto() {
        List<Resena> found = resenaRepository.findByProducto_IdProducto(producto.getIdProducto());
        assertFalse(found.isEmpty());
        assertEquals(producto.getIdProducto(), found.get(0).getProducto().getIdProducto());
    }

    @Test
    void shouldReturnPage_When_FindByProductoIdProductoWithPageable() {
        var page = resenaRepository.findByProducto_IdProducto(producto.getIdProducto(), PageRequest.of(0, 10));
        assertFalse(page.isEmpty());
        assertEquals(producto.getIdProducto(), page.getContent().get(0).getProducto().getIdProducto());
    }
}
