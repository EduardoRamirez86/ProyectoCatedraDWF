package sv.edu.udb.InvestigacionDwf.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import sv.edu.udb.InvestigacionDwf.model.entity.Producto;
import sv.edu.udb.InvestigacionDwf.model.entity.TipoProducto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private TipoProductoRepository tipoProductoRepository;

    private Long savedProductoId;
    private TipoProducto tipoProducto;

    @BeforeEach
    void init() {
        tipoProducto = tipoProductoRepository.save(TipoProducto.builder().tipo("Zapato").build());
        Producto producto = Producto.builder()
                .nombre("Zapato Deportivo")
                .precio(new BigDecimal("25.00"))
                .costo(new BigDecimal("15.00"))
                .cantidad(10)
                .cantidadPuntos(5)
                .tipoProducto(tipoProducto)
                .build();
        productoRepository.save(producto);
        savedProductoId = producto.getIdProducto();
    }

    @AfterEach
    void clean() {
        productoRepository.deleteAll();
        tipoProductoRepository.deleteAll();
    }

    @Test
    void testFindById() {
        Optional<Producto> producto = productoRepository.findById(savedProductoId);
        assertTrue(producto.isPresent());
        assertEquals("Zapato Deportivo", producto.get().getNombre());
    }

    @Test
    void testFindAll() {
        List<Producto> productos = productoRepository.findAll();
        assertNotNull(productos);
        assertFalse(productos.isEmpty());
    }

    @Test
    void testSave() {
        Producto producto = Producto.builder()
                .nombre("Zapato Casual")
                .precio(new BigDecimal("30.00"))
                .costo(new BigDecimal("20.00"))
                .cantidad(5)
                .cantidadPuntos(2)
                .tipoProducto(tipoProducto)
                .build();
        Producto saved = productoRepository.save(producto);
        assertNotNull(saved.getIdProducto());
        assertEquals("Zapato Casual", saved.getNombre());
    }

    @Test
    void testUpdate() {
        Optional<Producto> productoOpt = productoRepository.findById(savedProductoId);
        assertTrue(productoOpt.isPresent());
        Producto producto = productoOpt.get();
        producto.setCantidad(99);
        productoRepository.save(producto);

        Optional<Producto> updated = productoRepository.findById(savedProductoId);
        assertTrue(updated.isPresent());
        assertEquals(99, updated.get().getCantidad());
    }

    @Test
    void testDelete() {
        Optional<Producto> productoOpt = productoRepository.findById(savedProductoId);
        assertTrue(productoOpt.isPresent());
        productoRepository.delete(productoOpt.get());
        Optional<Producto> deleted = productoRepository.findById(savedProductoId);
        assertFalse(deleted.isPresent());
    }

    @Test
    void shouldSaveAndFindByNombre() {
        Optional<Producto> found = productoRepository.findByNombre("Zapato Deportivo");
        assertTrue(found.isPresent());
        assertEquals("Zapato Deportivo", found.get().getNombre());
    }

    @Test
    void shouldReturnEmpty_When_NombreNotExists() {
        Optional<Producto> found = productoRepository.findByNombre("NoExiste");
        assertFalse(found.isPresent());
    }
}
