package sv.edu.udb.InvestigacionDwf.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import sv.edu.udb.InvestigacionDwf.model.entity.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@ActiveProfiles("test")
class CarritoItemRepositoryTest {

    @Autowired
    private CarritoItemRepository carritoItemRepository;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private TipoProductoRepository tipoProductoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Long savedCarritoItemId;
    private Carrito carrito;
    private Producto producto;

    @BeforeEach
    void init() {
        // Solo crea el rol si no existe
        Role role = roleRepository.findByName("ROLE_USER").orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
        User user = userRepository.save(User.builder().username("itemuser").email("i@i.com").password("pass").primerNombre("a").primerApellido("b").role(role).build());
        carrito = carritoRepository.save(Carrito.builder().user(user).build());
        TipoProducto tipo = tipoProductoRepository.save(TipoProducto.builder().tipo("Accesorio").build());
        producto = productoRepository.save(Producto.builder().nombre("Prod").precio(BigDecimal.ONE).costo(BigDecimal.ONE).cantidad(1).cantidadPuntos(1).tipoProducto(tipo).build());
        CarritoItem item = new CarritoItem();
        item.setCarrito(carrito);
        item.setProducto(producto);
        item.setCantidad(2);
        carritoItemRepository.save(item);
        savedCarritoItemId = item.getIdCarritoItem();
    }

    @AfterEach
    void clean() {
        carritoItemRepository.deleteAll();
        carritoRepository.deleteAll();
        productoRepository.deleteAll();
        tipoProductoRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void testFindById() {
        Optional<CarritoItem> item = carritoItemRepository.findById(savedCarritoItemId);
        assertTrue(item.isPresent());
        assertEquals(carrito.getIdCarrito(), item.get().getCarrito().getIdCarrito());
    }

    @Test
    void testFindAll() {
        List<CarritoItem> items = carritoItemRepository.findAll();
        assertNotNull(items);
        assertFalse(items.isEmpty());
    }

    @Test
    void testSave() {
        CarritoItem item = new CarritoItem();
        item.setCarrito(carrito);
        item.setProducto(producto);
        item.setCantidad(5);
        CarritoItem saved = carritoItemRepository.save(item);
        assertNotNull(saved.getIdCarritoItem());
        assertEquals(5, saved.getCantidad());
    }

    @Test
    void testUpdate() {
        Optional<CarritoItem> itemOpt = carritoItemRepository.findById(savedCarritoItemId);
        assertTrue(itemOpt.isPresent());
        CarritoItem item = itemOpt.get();
        item.setCantidad(10);
        carritoItemRepository.save(item);

        Optional<CarritoItem> updated = carritoItemRepository.findById(savedCarritoItemId);
        assertTrue(updated.isPresent());
        assertEquals(10, updated.get().getCantidad());
    }

    @Test
    void testDelete() {
        Optional<CarritoItem> itemOpt = carritoItemRepository.findById(savedCarritoItemId);
        assertTrue(itemOpt.isPresent());
        carritoItemRepository.delete(itemOpt.get());
        Optional<CarritoItem> deleted = carritoItemRepository.findById(savedCarritoItemId);
        assertFalse(deleted.isPresent());
    }

    @Test
    void shouldSaveAndFindByCarritoIdCarrito() {
        // Solo crea el rol si no existe
        Role role = roleRepository.findByName("ROLE_USER").orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
        User user = userRepository.save(User.builder().username("itemuser2").email("i2@i.com").password("pass").primerNombre("a").primerApellido("b").role(role).build());
        Carrito carrito = carritoRepository.save(Carrito.builder().user(user).build());
        TipoProducto tipo = tipoProductoRepository.save(TipoProducto.builder().tipo("Accesorio2").build());
        Producto producto = productoRepository.save(Producto.builder().nombre("Prod2").precio(BigDecimal.ONE).costo(BigDecimal.ONE).cantidad(1).cantidadPuntos(1).tipoProducto(tipo).build());
        CarritoItem item = new CarritoItem();
        item.setCarrito(carrito);
        item.setProducto(producto);
        item.setCantidad(2);
        carritoItemRepository.save(item);

        List<CarritoItem> items = carritoItemRepository.findByCarrito_IdCarrito(carrito.getIdCarrito());
        assertFalse(items.isEmpty());
        assertEquals(carrito.getIdCarrito(), items.get(0).getCarrito().getIdCarrito());
    }
}
