package sv.edu.udb.InvestigacionDwf.model;

import org.junit.jupiter.api.Test;
import sv.edu.udb.InvestigacionDwf.model.entity.*;
import sv.edu.udb.InvestigacionDwf.model.enums.EstadoNotificacion;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificacionTest {

    @Test
    void testConstructorAndGetters() {
        User user = User.builder().idUser(1L).build();
        Pedido pedido = Pedido.builder().idPedido(1L).build();
        LocalDateTime fecha = LocalDateTime.now();
        Notificacion notif = Notificacion.builder()
                .id(1L)
                .user(user)
                .mensaje("Mensaje de prueba")
                .fechaEnvio(fecha)
                .estado(EstadoNotificacion.ENVIADA)
                .pedido(pedido)
                .build();

        assertEquals(1L, notif.getId());
        assertEquals(user, notif.getUser());
        assertEquals("Mensaje de prueba", notif.getMensaje());
        assertEquals(fecha, notif.getFechaEnvio());
        assertEquals(EstadoNotificacion.ENVIADA, notif.getEstado());
        assertEquals(pedido, notif.getPedido());
    }

    @Test
    void testSetters() {
        Notificacion notif = new Notificacion();
        User user = new User();
        user.setIdUser(2L);
        Pedido pedido = new Pedido();
        pedido.setIdPedido(2L);
        notif.setId(2L);
        notif.setUser(user);
        notif.setMensaje("Otro mensaje");
        notif.setFechaEnvio(LocalDateTime.now());
        notif.setEstado(EstadoNotificacion.LEIDA);
        notif.setPedido(pedido);

        assertEquals(2L, notif.getId());
        assertEquals(user, notif.getUser());
        assertEquals("Otro mensaje", notif.getMensaje());
        assertEquals(EstadoNotificacion.LEIDA, notif.getEstado());
        assertEquals(pedido, notif.getPedido());
    }

    @Test
    void testRelacionesBidireccionales() {
        User user = new User();
        user.setIdUser(1L);
        Pedido pedido = new Pedido();
        pedido.setIdPedido(1L);

        Notificacion notif = new Notificacion();
        notif.setId(1L);
        notif.setUser(user);
        notif.setPedido(pedido);

        assertNotNull(notif.getUser());
        assertEquals(1L, notif.getUser().getIdUser().longValue());
        assertNotNull(notif.getPedido());
        assertEquals(1L, notif.getPedido().getIdPedido().longValue());
    }
}
