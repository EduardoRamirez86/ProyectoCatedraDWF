// src/main/java/sv/edu/udb/InvestigacionDwf/service/impl/PedidoServiceImpl.java
package sv.edu.udb.InvestigacionDwf.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.InvestigacionDwf.dto.request.PagoRequest;
import sv.edu.udb.InvestigacionDwf.dto.request.PedidoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.PedidoResponse;
import sv.edu.udb.InvestigacionDwf.exception.ResourceNotFoundException;
import sv.edu.udb.InvestigacionDwf.model.entity.HistorialPedido;
import sv.edu.udb.InvestigacionDwf.model.entity.HistorialPuntos;
import sv.edu.udb.InvestigacionDwf.model.entity.Pedido;
import sv.edu.udb.InvestigacionDwf.model.entity.User;
import sv.edu.udb.InvestigacionDwf.model.enums.EstadoPedido;
import sv.edu.udb.InvestigacionDwf.model.enums.TipoPago;
import sv.edu.udb.InvestigacionDwf.repository.PedidoRepository;
import sv.edu.udb.InvestigacionDwf.service.mapper.PedidoMapper;
import sv.edu.udb.InvestigacionDwf.repository.CarritoRepository;
import sv.edu.udb.InvestigacionDwf.repository.CarritoItemRepository;
import sv.edu.udb.InvestigacionDwf.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements sv.edu.udb.InvestigacionDwf.service.PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final PedidoMapper pedidoMapper;
    private final UserRepository userRepository; // Se agregó para guardar cambios en el usuario

    @Override
    @Transactional
    public PedidoResponse checkout(PedidoRequest req) {
        var carrito = carritoRepository.findById(req.getIdCarrito())
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado ID: " + req.getIdCarrito()));

        var items = carritoItemRepository.findByCarrito_IdCarrito(carrito.getIdCarrito());
        BigDecimal total = items.stream()
                .map(i -> i.getProducto().getPrecio().multiply(BigDecimal.valueOf(i.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Pedido pedido = new Pedido();
        pedido.setCarrito(carrito);
        pedido.setFechaInicio(LocalDateTime.now());
        pedido.setTotal(total);
        pedido.setTipoPago(req.getTipoPago()); // enum TipoPago
        pedido.getHistorialPedidos().clear();
        pedido.actualizarEstado(EstadoPedido.PENDIENTE, carrito.getUser());

        // -------- NUEVA LÓGICA DE PUNTOS --------
        // Calcular puntos ganados por el pedido
        int puntosGanados = items.stream()
                .mapToInt(item -> item.getProducto().getCantidadPuntos() * item.getCantidad())
                .sum();
        pedido.setPuntosTotales(puntosGanados); // guardar en el pedido

        // Crear historial de puntos
        User user = carrito.getUser();
        int puntosAntes = user.getPuntos();
        int puntosNuevos = puntosAntes + puntosGanados;
        user.setPuntos(puntosNuevos); // actualizar puntos del usuario

        HistorialPuntos historial = new HistorialPuntos();
        historial.setPedido(pedido);
        historial.setUser(user);
        historial.setFecha(LocalDateTime.now());
        historial.setCantidadAnterior(puntosAntes);
        historial.setCantidadNueva(puntosNuevos);
        pedido.getHistorialPuntos().add(historial); // asociar historial al pedido
        // -------- FIN NUEVA LÓGICA DE PUNTOS --------

        var saved = pedidoRepository.save(pedido);
        userRepository.save(user); // guardar cambios del usuario también
        return pedidoMapper.toResponse(saved);
    }

    private Pedido getPedidoOrThrow(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado ID: " + id));
    }

    @Override
    @Transactional
    public PedidoResponse confirmar(Long idPedido) {
        Pedido p = getPedidoOrThrow(idPedido);
        p.actualizarEstado(EstadoPedido.PAGADO, p.getCarrito().getUser());
        p.setFechaFinal(null);
        return pedidoMapper.toResponse(pedidoRepository.save(p));
    }

    @Override
    @Transactional
    public PedidoResponse pagar(Long idPedido, PagoRequest pagoReq) {
        Pedido p = getPedidoOrThrow(idPedido);
        // Aquí lógica real de pasarela de pago...
        p.actualizarEstado(EstadoPedido.PAGADO, pagoReq.getUsuario());
        return pedidoMapper.toResponse(pedidoRepository.save(p));
    }

    @Override
    @Transactional
    public PedidoResponse iniciarEnvio(Long idPedido) {
        Pedido p = getPedidoOrThrow(idPedido);
        p.actualizarEstado(EstadoPedido.EN_PROCESO, p.getCarrito().getUser());
        return pedidoMapper.toResponse(pedidoRepository.save(p));
    }

    @Override
    @Transactional
    public PedidoResponse marcarEntregado(Long idPedido) {
        Pedido p = getPedidoOrThrow(idPedido);
        p.actualizarEstado(EstadoPedido.ENTREGADO, p.getCarrito().getUser());
        p.setFechaFinal(LocalDateTime.now());
        return pedidoMapper.toResponse(pedidoRepository.save(p));
    }

    @Override
    @Transactional
    public PedidoResponse cancelar(Long idPedido, String motivo) {
        Pedido p = getPedidoOrThrow(idPedido);
        p.actualizarEstado(EstadoPedido.CANCELADO, p.getCarrito().getUser());
        p.setFechaFinal(LocalDateTime.now());
        // opcional: guardar motivo en un nuevo campo
        return pedidoMapper.toResponse(pedidoRepository.save(p));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponse> findAllByUser(Long idUser) {
        return pedidoRepository.findByCarrito_User_IdUser(idUser).stream()
                .map(pedidoMapper::toResponse)
                .collect(Collectors.toList());
    }
}
