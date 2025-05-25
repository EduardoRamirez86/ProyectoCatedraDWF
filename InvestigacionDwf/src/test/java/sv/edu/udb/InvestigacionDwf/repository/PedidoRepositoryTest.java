package sv.edu.udb.InvestigacionDwf.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import sv.edu.udb.InvestigacionDwf.model.entity.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static sv.edu.udb.InvestigacionDwf.model.enums.EstadoPedido.PENDIENTE;
import static sv.edu.udb.InvestigacionDwf.model.enums.TipoPago.EFECTIVO;

@DataJpaTest
class PedidoRepositoryTest {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Long savedPedidoId;
    private Carrito carrito;
    private User user;

    @BeforeEach
    void init() {
        Role role = roleRepository.save(new Role("ROLE_USER"));
        user = userRepository.save(User.builder().username("pedidouser").email("p@p.com").password("pass").primerNombre("a").primerApellido("b").role(role).build());
        carrito = carritoRepository.save(Carrito.builder().user(user).build());
        Pedido pedido = Pedido.builder()
                .carrito(carrito)
                .fechaInicio(LocalDateTime.now())
                .total(BigDecimal.ONE)
                .puntosTotales(1)
                .estado(PENDIENTE)
                .tipoPago(EFECTIVO)
                .build();
        pedidoRepository.save(pedido);
        savedPedidoId = pedido.getIdPedido();
    }

    @AfterEach
    void clean() {
        pedidoRepository.deleteAll();
        carritoRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void testFindById() {
        Optional<Pedido> pedido = pedidoRepository.findById(savedPedidoId);
        assertTrue(pedido.isPresent());
        assertEquals(carrito.getIdCarrito(), pedido.get().getCarrito().getIdCarrito());
    }

    @Test
    void testFindAll() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        assertNotNull(pedidos);
        assertFalse(pedidos.isEmpty());
    }

    @Test
    void testSave() {
        Pedido pedido = Pedido.builder()
                .carrito(carrito)
                .fechaInicio(LocalDateTime.now())
                .total(BigDecimal.TEN)
                .puntosTotales(5)
                .estado(PENDIENTE)
                .tipoPago(EFECTIVO)
                .build();
        Pedido saved = pedidoRepository.save(pedido);
        assertNotNull(saved.getIdPedido());
        assertEquals(carrito.getIdCarrito(), saved.getCarrito().getIdCarrito());
    }

    @Test
    void testUpdate() {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(savedPedidoId);
        assertTrue(pedidoOpt.isPresent());
        Pedido pedido = pedidoOpt.get();
        pedido.setPuntosTotales(99);
        pedidoRepository.save(pedido);

        Optional<Pedido> updated = pedidoRepository.findById(savedPedidoId);
        assertTrue(updated.isPresent());
        assertEquals(99, updated.get().getPuntosTotales());
    }

    @Test
    void testDelete() {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(savedPedidoId);
        assertTrue(pedidoOpt.isPresent());
        pedidoRepository.delete(pedidoOpt.get());
        Optional<Pedido> deleted = pedidoRepository.findById(savedPedidoId);
        assertFalse(deleted.isPresent());
    }

    @Test
    void shouldSaveAndFindByCarritoUserIdUser() {
        var page = pedidoRepository.findByCarrito_User_IdUser(user.getIdUser(), PageRequest.of(0, 10));
        assertFalse(page.isEmpty());
        assertEquals(carrito.getIdCarrito(), page.getContent().get(0).getCarrito().getIdCarrito());
    }
}
