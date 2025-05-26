package sv.edu.udb.InvestigacionDwf.model;

import org.junit.jupiter.api.Test;
import sv.edu.udb.InvestigacionDwf.model.entity.Carrito;
import sv.edu.udb.InvestigacionDwf.model.entity.User;
import sv.edu.udb.InvestigacionDwf.model.entity.HistorialPedido;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CarritoTest {

    @Test
    void testConstructorAndGetters() {
        User user = User.builder().idUser(1L).build();
        LocalDateTime fecha = LocalDateTime.now();
        Carrito carrito = Carrito.builder()
                .idCarrito(1L)
                .user(user)
                .fechaCreacion(fecha)
                .build();

        assertEquals(1L, carrito.getIdCarrito());
        assertEquals(user, carrito.getUser());
        assertEquals(fecha, carrito.getFechaCreacion());
    }

    @Test
    void testSetters() {
        Carrito carrito = new Carrito();
        User user = new User();
        user.setIdUser(2L);
        LocalDateTime fecha = LocalDateTime.now();
        carrito.setIdCarrito(2L);
        carrito.setUser(user);
        carrito.setFechaCreacion(fecha);

        assertEquals(2L, carrito.getIdCarrito());
        assertEquals(user, carrito.getUser());
        assertEquals(fecha, carrito.getFechaCreacion());
    }

    @Test
    void testRelacionesBidireccionales() {
        // Crear objetos relacionados
        User user = new User();
        user.setIdUser(1L);

        Carrito carrito = new Carrito();
        carrito.setIdCarrito(1L);
        carrito.setUser(user); // Relación inversa

        HistorialPedido pedido = new HistorialPedido();
        pedido.setIdHistorialPedido(1L);
        pedido.setUser(user);  // Relación inversa

        // Verificar que las relaciones están bien configuradas
        assertNotNull(carrito.getUser());
        assertEquals(1L, carrito.getUser().getIdUser().longValue());

        assertNotNull(pedido.getUser());
        assertEquals(1L, pedido.getUser().getIdUser().longValue());
    }
}
