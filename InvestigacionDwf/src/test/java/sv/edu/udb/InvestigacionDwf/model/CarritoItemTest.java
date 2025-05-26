package sv.edu.udb.InvestigacionDwf.model;

import org.junit.jupiter.api.Test;
import sv.edu.udb.InvestigacionDwf.model.entity.Carrito;
import sv.edu.udb.InvestigacionDwf.model.entity.CarritoItem;
import sv.edu.udb.InvestigacionDwf.model.entity.Producto;

import static org.junit.jupiter.api.Assertions.*;

class CarritoItemTest {

    @Test
    void testConstructorAndGetters() {
        Carrito carrito = Carrito.builder().idCarrito(1L).build();
        Producto producto = Producto.builder().idProducto(1L).build();
        CarritoItem item = new CarritoItem();
        item.setIdCarritoItem(1L);
        item.setCarrito(carrito);
        item.setProducto(producto);
        item.setCantidad(3);

        assertEquals(1L, item.getIdCarritoItem());
        assertEquals(carrito, item.getCarrito());
        assertEquals(producto, item.getProducto());
        assertEquals(3, item.getCantidad());
    }

    @Test
    void testSetters() {
        CarritoItem item = new CarritoItem();
        Carrito carrito = new Carrito();
        Producto producto = new Producto();

        item.setIdCarritoItem(2L);
        item.setCarrito(carrito);
        item.setProducto(producto);
        item.setCantidad(5);

        assertEquals(2L, item.getIdCarritoItem());
        assertEquals(carrito, item.getCarrito());
        assertEquals(producto, item.getProducto());
        assertEquals(5, item.getCantidad());
    }

}
