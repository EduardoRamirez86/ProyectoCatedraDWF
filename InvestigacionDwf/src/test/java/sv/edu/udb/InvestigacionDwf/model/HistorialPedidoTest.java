package sv.edu.udb.InvestigacionDwf.model;

import org.junit.jupiter.api.Test;
import sv.edu.udb.InvestigacionDwf.model.entity.*;
import sv.edu.udb.InvestigacionDwf.model.enums.EstadoPedido;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HistorialPedidoTest {

    @Test
    void testConstructorAndGetters() {
        Pedido pedido = Pedido.builder().idPedido(1L).build();
        User user = User.builder().idUser(1L).build();
        LocalDateTime fecha = LocalDateTime.now();
        HistorialPedido historial = HistorialPedido.builder()
                .idHistorialPedido(1L)
                .pedido(pedido)
                .user(user)
                .estado(EstadoPedido.PAGADO)
                .fecha(fecha)
                .descripcion("Pago realizado")
                .build();

        assertEquals(1L, historial.getIdHistorialPedido());
        assertEquals(pedido, historial.getPedido());
        assertEquals(user, historial.getUser());
        assertEquals(EstadoPedido.PAGADO, historial.getEstado());
        assertEquals(fecha, historial.getFecha());
        assertEquals("Pago realizado", historial.getDescripcion());
    }

    @Test
    void testSetters() {
        HistorialPedido historial = new HistorialPedido();
        Pedido pedido = new Pedido();
        pedido.setIdPedido(2L);
        User user = new User();
        user.setIdUser(2L);
        LocalDateTime fecha = LocalDateTime.now();
        historial.setIdHistorialPedido(2L);
        historial.setPedido(pedido);
        historial.setUser(user);
        historial.setEstado(EstadoPedido.ENVIADO);
        historial.setFecha(fecha);
        historial.setDescripcion("Enviado");

        assertEquals(2L, historial.getIdHistorialPedido());
        assertEquals(pedido, historial.getPedido());
        assertEquals(user, historial.getUser());
        assertEquals(EstadoPedido.ENVIADO, historial.getEstado());
        assertEquals(fecha, historial.getFecha());
        assertEquals("Enviado", historial.getDescripcion());
    }

    @Test
    void testRelacionesBidireccionales() {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(1L);
        User user = new User();
        user.setIdUser(1L);

        HistorialPedido historial = new HistorialPedido();
        historial.setIdHistorialPedido(1L);
        historial.setPedido(pedido);
        historial.setUser(user);

        assertNotNull(historial.getPedido());
        assertEquals(1L, historial.getPedido().getIdPedido().longValue());
        assertNotNull(historial.getUser());
        assertEquals(1L, historial.getUser().getIdUser().longValue());
    }
}
