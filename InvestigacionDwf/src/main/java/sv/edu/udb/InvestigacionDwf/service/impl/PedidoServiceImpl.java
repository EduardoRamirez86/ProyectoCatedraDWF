// src/main/java/sv/edu/udb/InvestigacionDwf/service/impl/PedidoServiceImpl.java
package sv.edu.udb.InvestigacionDwf.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.InvestigacionDwf.dto.request.PagoRequest;
import sv.edu.udb.InvestigacionDwf.dto.request.PedidoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.PedidoResponse;
import sv.edu.udb.InvestigacionDwf.exception.ResourceNotFoundException;
import sv.edu.udb.InvestigacionDwf.model.entity.HistorialPuntos;
import sv.edu.udb.InvestigacionDwf.model.entity.Notificacion;
import sv.edu.udb.InvestigacionDwf.model.entity.Pedido;
import sv.edu.udb.InvestigacionDwf.model.entity.User;
import sv.edu.udb.InvestigacionDwf.model.enums.EstadoNotificacion;
import sv.edu.udb.InvestigacionDwf.model.enums.EstadoPedido;
import sv.edu.udb.InvestigacionDwf.repository.CarritoItemRepository;
import sv.edu.udb.InvestigacionDwf.repository.CarritoRepository;
import sv.edu.udb.InvestigacionDwf.repository.NotificacionRepository;
import sv.edu.udb.InvestigacionDwf.repository.PedidoRepository;
import sv.edu.udb.InvestigacionDwf.repository.UserRepository;
import sv.edu.udb.InvestigacionDwf.service.mapper.PedidoMapper;

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
    private final UserRepository userRepository;
    private final NotificacionRepository notificacionRepository;  // <-- inyectado

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
        pedido.setTipoPago(req.getTipoPago());
        pedido.getHistorialPedidos().clear();
        pedido.actualizarEstado(EstadoPedido.PENDIENTE, carrito.getUser());

        // lógica de puntos...
        int puntosGanados = items.stream()
                .mapToInt(item -> item.getProducto().getCantidadPuntos() * item.getCantidad())
                .sum();
        pedido.setPuntosTotales(puntosGanados);
        User user = carrito.getUser();
        int puntosAntes = user.getPuntos();
        int puntosNuevos = puntosAntes + puntosGanados;
        user.setPuntos(puntosNuevos);

        HistorialPuntos historialPuntos = new HistorialPuntos();
        historialPuntos.setPedido(pedido);
        historialPuntos.setUser(user);
        historialPuntos.setFecha(LocalDateTime.now());
        historialPuntos.setCantidadAnterior(puntosAntes);
        historialPuntos.setCantidadNueva(puntosNuevos);
        pedido.getHistorialPuntos().add(historialPuntos);

        // guardamos pedido y usuario
        var savedPedido = pedidoRepository.save(pedido);
        userRepository.save(user);

        // **CREAR Y GUARDAR NOTIFICACIÓN**
        crearNotificacion(user, savedPedido, "PENDIENTE");

        return pedidoMapper.toResponse(savedPedido);
    }

    @Override
    @Transactional
    public PedidoResponse confirmar(Long idPedido) {
        Pedido p = getPedidoOrThrow(idPedido);
        p.actualizarEstado(EstadoPedido.PAGADO, p.getCarrito().getUser());
        p.setFechaFinal(null);
        var saved = pedidoRepository.save(p);

        crearNotificacion(p.getCarrito().getUser(), saved, "PAGADO");
        return pedidoMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PedidoResponse pagar(Long idPedido, PagoRequest pagoReq) {
        Pedido p = getPedidoOrThrow(idPedido);
        p.actualizarEstado(EstadoPedido.PAGADO, pagoReq.getUsuario());
        var saved = pedidoRepository.save(p);

        crearNotificacion(p.getCarrito().getUser(), saved, "PAGADO");
        return pedidoMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PedidoResponse iniciarEnvio(Long idPedido) {
        Pedido p = getPedidoOrThrow(idPedido);
        p.actualizarEstado(EstadoPedido.EN_PROCESO, p.getCarrito().getUser());
        var saved = pedidoRepository.save(p);

        crearNotificacion(p.getCarrito().getUser(), saved, "EN_PROCESO");
        return pedidoMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PedidoResponse marcarEntregado(Long idPedido) {
        Pedido p = getPedidoOrThrow(idPedido);
        p.actualizarEstado(EstadoPedido.ENTREGADO, p.getCarrito().getUser());
        p.setFechaFinal(LocalDateTime.now());
        var saved = pedidoRepository.save(p);

        crearNotificacion(p.getCarrito().getUser(), saved, "ENTREGADO");
        return pedidoMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PedidoResponse cancelar(Long idPedido, String motivo) {
        Pedido p = getPedidoOrThrow(idPedido);
        p.actualizarEstado(EstadoPedido.CANCELADO, p.getCarrito().getUser());
        p.setFechaFinal(LocalDateTime.now());
        var saved = pedidoRepository.save(p);

        crearNotificacion(p.getCarrito().getUser(), saved, "CANCELADO");
        return pedidoMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponse> findAllByUser(Long idUser) {
        return pedidoRepository.findByCarrito_User_IdUser(idUser).stream()
                .map(pedidoMapper::toResponse)
                .collect(Collectors.toList());
    }

    private Pedido getPedidoOrThrow(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado ID: " + id));
    }

    /** Método helper para crear y guardar la notificación */
    private void crearNotificacion(User user, Pedido pedido, String estadoStr) {
        Notificacion notif = new Notificacion();
        notif.setUser(user);
        notif.setMensaje("Estado del pedido #" + pedido.getIdPedido() + " actualizado a: " + estadoStr);
        notif.setFechaEnvio(LocalDateTime.now());
        notif.setEstado(EstadoNotificacion.ENVIADA);
        notif.setPedido(pedido);
        notificacionRepository.save(notif);
    }
    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponse> findAll() {
        return pedidoRepository.findAll().stream()
                .map(pedidoMapper::toResponse)
                .collect(Collectors.toList());
    }

}

