package sv.edu.udb.InvestigacionDwf.model;

import org.junit.jupiter.api.Test;
import sv.edu.udb.InvestigacionDwf.model.entity.Producto;
import sv.edu.udb.InvestigacionDwf.model.entity.TipoProducto;
import sv.edu.udb.InvestigacionDwf.model.entity.CarritoItem;
import sv.edu.udb.InvestigacionDwf.model.entity.Resena;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductoTest {

    @Test
    void testConstructorAndGetters() {
        TipoProducto tipo = TipoProducto.builder().idTipoProducto(1L).tipo("Camisa").build();
        Producto producto = Producto.builder()
                .idProducto(1L)
                .nombre("Camisa Polo")
                .descripcion("Camisa de algodón")
                .precio(new BigDecimal("19.99"))
                .costo(new BigDecimal("10.00"))
                .cantidad(5)
                .imagen("img.jpg")
                .cantidadPuntos(10)
                .tipoProducto(tipo)
                .build();

        assertEquals(1L, producto.getIdProducto());
        assertEquals("Camisa Polo", producto.getNombre());
        assertEquals("Camisa de algodón", producto.getDescripcion());
        assertEquals(new BigDecimal("19.99"), producto.getPrecio());
        assertEquals(new BigDecimal("10.00"), producto.getCosto());
        assertEquals(5, producto.getCantidad());
        assertEquals("img.jpg", producto.getImagen());
        assertEquals(10, producto.getCantidadPuntos());
        assertNotNull(producto.getTipoProducto());
        assertEquals("Camisa", producto.getTipoProducto().getTipo());
    }

    @Test
    void testSetters() {
        Producto producto = new Producto();
        producto.setIdProducto(2L);
        producto.setNombre("Zapatos");
        producto.setDescripcion("Zapatos de cuero");
        producto.setPrecio(new BigDecimal("50.00"));
        producto.setCosto(new BigDecimal("30.00"));
        producto.setCantidad(2);
        producto.setImagen("zapatos.jpg");
        producto.setCantidadPuntos(5);
        assertEquals(2L, producto.getIdProducto());
        assertEquals("Zapatos", producto.getNombre());
        assertEquals("Zapatos de cuero", producto.getDescripcion());
        assertEquals(new BigDecimal("50.00"), producto.getPrecio());
        assertEquals(new BigDecimal("30.00"), producto.getCosto());
        assertEquals(2, producto.getCantidad());
        assertEquals("zapatos.jpg", producto.getImagen());
        assertEquals(5, producto.getCantidadPuntos());
    }

    @Test
    void testActualizarStock() {
        Producto producto = Producto.builder().cantidad(5).build();
        producto.actualizarStock(10);
        assertEquals(10, producto.getCantidad());
    }

    @Test
    void testRelacionesBidireccionales() {
        Producto producto = new Producto();
        CarritoItem item = new CarritoItem();
        Resena resena = new Resena();
        producto.setCarritoItems(List.of(item));
        producto.setResenas(List.of(resena));
        assertNotNull(producto.getCarritoItems());
        assertNotNull(producto.getResenas());
        assertTrue(producto.getCarritoItems().contains(item));
        assertTrue(producto.getResenas().contains(resena));
    }
}
