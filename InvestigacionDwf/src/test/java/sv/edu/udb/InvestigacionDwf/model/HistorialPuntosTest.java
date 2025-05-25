package sv.edu.udb.InvestigacionDwf.model;

import org.junit.jupiter.api.Test;
import sv.edu.udb.InvestigacionDwf.model.entity.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HistorialPuntosTest {

    @Test
    void testConstructorAndGetters() {
        User user = User.builder().idUser(1L).build();
        Pedido pedido = Pedido.builder().idPedido(1L).build();
        LocalDateTime fecha = LocalDateTime.now();
        HistorialPuntos historial = HistorialPuntos.builder()
                .idHistorialPuntos(1L)
                .user(user)
                .pedido(pedido)
                .fecha(fecha)
                .cantidadAnterior(100)
                .cantidadNueva(120)
                .build();

        assertEquals(1L, historial.getIdHistorialPuntos());
        assertEquals(user, historial.getUser());
        assertEquals(pedido, historial.getPedido());
        assertEquals(fecha, historial.getFecha());
        assertEquals(100, historial.getCantidadAnterior());
        assertEquals(120, historial.getCantidadNueva());
    }

    @Test
    void testSetters() {
        HistorialPuntos historial = new HistorialPuntos();
        User user = new User();
        user.setIdUser(2L);
        Pedido pedido = new Pedido();
        pedido.setIdPedido(2L);
        LocalDateTime fecha = LocalDateTime.now();
        historial.setIdHistorialPuntos(2L);
        historial.setUser(user);
        historial.setPedido(pedido);
        historial.setFecha(fecha);
        historial.setCantidadAnterior(50);
        historial.setCantidadNueva(70);

        assertEquals(2L, historial.getIdHistorialPuntos());
        assertEquals(user, historial.getUser());
        assertEquals(pedido, historial.getPedido());
        assertEquals(fecha, historial.getFecha());
        assertEquals(50, historial.getCantidadAnterior());
        assertEquals(70, historial.getCantidadNueva());
    }

    @Test
    void testRelacionesBidireccionales() {
        User user = new User();
        user.setIdUser(1L);
        Pedido pedido = new Pedido();
        pedido.setIdPedido(1L);

        HistorialPuntos historial = new HistorialPuntos();
        historial.setIdHistorialPuntos(1L);
        historial.setUser(user);
        historial.setPedido(pedido);

        assertNotNull(historial.getUser());
        assertEquals(1L, historial.getUser().getIdUser().longValue());
        assertNotNull(historial.getPedido());
        assertEquals(1L, historial.getPedido().getIdPedido().longValue());
    }
}
