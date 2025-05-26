package sv.edu.udb.InvestigacionDwf.model;

import org.junit.jupiter.api.Test;
import sv.edu.udb.InvestigacionDwf.model.entity.*;
import sv.edu.udb.InvestigacionDwf.model.enums.EstadoPedido;
import sv.edu.udb.InvestigacionDwf.model.enums.TipoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PedidoTest {

    @Test
    void testConstructorAndGetters() {
        Carrito carrito = Carrito.builder().idCarrito(1L).build();
        Direccion direccion = Direccion.builder().idDireccion(1L).build();
        LocalDateTime fechaInicio = LocalDateTime.now();
        Pedido pedido = Pedido.builder()
                .idPedido(1L)
                .fechaInicio(fechaInicio)
                .total(new BigDecimal("100.00"))
                .puntosTotales(10)
                .estado(EstadoPedido.PENDIENTE)
                .tipoPago(TipoPago.EFECTIVO)
                .carrito(carrito)
                .direccion(direccion)
                .build();

        assertEquals(1L, pedido.getIdPedido());
        assertEquals(fechaInicio, pedido.getFechaInicio());
        assertEquals(new BigDecimal("100.00"), pedido.getTotal());
        assertEquals(10, pedido.getPuntosTotales());
        assertEquals(EstadoPedido.PENDIENTE, pedido.getEstado());
        assertEquals(TipoPago.EFECTIVO, pedido.getTipoPago());
        assertEquals(carrito, pedido.getCarrito());
        assertEquals(direccion, pedido.getDireccion());
    }

    @Test
    void testActualizarEstado() {
        Pedido pedido = Pedido.builder().estado(EstadoPedido.PENDIENTE).build();
        User user = User.builder().idUser(1L).build();
        pedido.actualizarEstado(EstadoPedido.ENVIADO, user);

        assertEquals(EstadoPedido.ENVIADO, pedido.getEstado());
        assertFalse(pedido.getHistorialPedidos().isEmpty());
        assertEquals(user, pedido.getHistorialPedidos().get(0).getUser());
    }
}
