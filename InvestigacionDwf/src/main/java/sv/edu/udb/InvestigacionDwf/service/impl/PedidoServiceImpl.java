// src/main/java/sv/edu/udb/InvestigacionDwf/service/impl/PedidoServiceImpl.java
package sv.edu.udb.InvestigacionDwf.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.InvestigacionDwf.dto.request.PagoRequest;
import sv.edu.udb.InvestigacionDwf.dto.request.PedidoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.PedidoResponse;
import sv.edu.udb.InvestigacionDwf.exception.ResourceNotFoundException;
import sv.edu.udb.InvestigacionDwf.model.entity.*;
import sv.edu.udb.InvestigacionDwf.model.enums.EstadoNotificacion;
import sv.edu.udb.InvestigacionDwf.model.enums.EstadoPedido;
import sv.edu.udb.InvestigacionDwf.repository.*;
import sv.edu.udb.InvestigacionDwf.service.CuponService;
import sv.edu.udb.InvestigacionDwf.service.mapper.PedidoMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements sv.edu.udb.InvestigacionDwf.service.PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final DireccionRepository direccionRepository;
    private final PedidoMapper pedidoMapper;
    private final UserRepository userRepository;
    private final NotificacionRepository notificacionRepository;
    private final CuponService cuponService;
    private final CuponRepository cuponRepository;

    @Override
    @Transactional
    public PedidoResponse checkout(PedidoRequest req) {
        // Buscar el carrito
        Carrito carrito = carritoRepository.findById(req.getIdCarrito())
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado ID: " + req.getIdCarrito()));

        // Verificar que el carrito no esté vacío
        List<CarritoItem> items = carritoItemRepository.findByCarrito_IdCarrito(carrito.getIdCarrito());
        if (items.isEmpty()) {
            throw new IllegalStateException("No se puede realizar la compra con el carrito vacío.");
        }

        // Calcular el total
        BigDecimal total = items.stream()
                .map(i -> i.getProducto().getPrecio().multiply(BigDecimal.valueOf(i.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Obtener el usuario
        User user = carrito.getUser();

        // Manejar cupones si se proporcionan
        if (req.getCuponCodigo() != null && !req.getCuponCodigo().isEmpty()) {
            if (cuponService.validateCoupon(req.getCuponCodigo(), user)) {
                Cupon cupon = cuponRepository.findByCodigo(req.getCuponCodigo())
                        .orElseThrow(() -> new ResourceNotFoundException("Cupón no encontrado: " + req.getCuponCodigo()));
                BigDecimal descuento = total.multiply(BigDecimal.valueOf(cupon.getPorcentajeDescuento()).divide(BigDecimal.valueOf(100)));
                total = total.subtract(descuento);
                cuponService.redeemCoupon(req.getCuponCodigo(), user);
            } else {
                throw new IllegalStateException("Cupón inválido o no aplicable");
            }
        }

        // Crear el pedido
        Pedido pedido = new Pedido();
        pedido.setCarrito(carrito);
        pedido.setFechaInicio(LocalDateTime.now());
        pedido.setTotal(total);
        pedido.setTipoPago(req.getTipoPago());

        // Inicializar listas si están nulas
        if (pedido.getHistorialPedidos() == null) {
            pedido.setHistorialPedidos(new ArrayList<>());
        }
        if (pedido.getHistorialPuntos() == null) {
            pedido.setHistorialPuntos(new ArrayList<>());
        }

        // Establecer estado inicial
        pedido.actualizarEstado(EstadoPedido.PENDIENTE, user);

        // Validar y asignar la dirección
        if (req.getIdDireccion() == null) {
            throw new IllegalArgumentException("Debe especificar una dirección de entrega.");
        }
        Direccion dir = direccionRepository.findById(req.getIdDireccion())
                .orElseThrow(() -> new ResourceNotFoundException("Dirección no encontrada ID: " + req.getIdDireccion()));
        pedido.setDireccion(dir);

        // Calcular puntos ganados
        int puntosGanados = items.stream()
                .mapToInt(i -> i.getProducto().getCantidadPuntos() * i.getCantidad())
                .sum();
        pedido.setPuntosTotales(puntosGanados);

        // Actualizar puntos del usuario
        int puntosAntes = user.getPuntos();
        int puntosNuevos = puntosAntes + puntosGanados;
        user.setPuntos(puntosNuevos);

        // Registrar historial de puntos
        HistorialPuntos hp = new HistorialPuntos();
        hp.setPedido(pedido);
        hp.setUser(user);
        hp.setFecha(LocalDateTime.now());
        hp.setCantidadAnterior(puntosAntes);
        hp.setCantidadNueva(puntosNuevos);
        pedido.getHistorialPuntos().add(hp);

        // Guardar pedido y usuario
        Pedido saved = pedidoRepository.save(pedido);
        userRepository.save(user);

        // Generar cupón si se alcanzan 30 puntos
        if (puntosNuevos >= 30) {
            Cupon nuevoCupon = cuponService.generateCouponForUser(user);
            user.setPuntos(puntosNuevos - 30);

            HistorialPuntos hp2 = new HistorialPuntos();
            hp2.setPedido(saved);
            hp2.setUser(user);
            hp2.setFecha(LocalDateTime.now());
            hp2.setCantidadAnterior(puntosNuevos);
            hp2.setCantidadNueva(user.getPuntos());
            saved.getHistorialPuntos().add(hp2);
            userRepository.save(user);
            crearNotificacionCupon(user, saved, nuevoCupon.getCodigo());
        }

        // Crear notificación de estado
        crearNotificacion(user, saved, "PENDIENTE");

        return pedidoMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PedidoResponse confirmar(Long idPedido) {
        Pedido p = getPedidoOrThrow(idPedido);
        p.actualizarEstado(EstadoPedido.PAGADO, p.getCarrito().getUser());
        p.setFechaFinal(null);
        Pedido saved = pedidoRepository.save(p);
        crearNotificacion(p.getCarrito().getUser(), saved, "PAGADO");
        return pedidoMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PedidoResponse pagar(Long idPedido, PagoRequest pagoReq) {
        Pedido p = getPedidoOrThrow(idPedido);
        p.actualizarEstado(EstadoPedido.PAGADO, pagoReq.getUsuario());
        Pedido saved = pedidoRepository.save(p);
        crearNotificacion(p.getCarrito().getUser(), saved, "PAGADO");
        return pedidoMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PedidoResponse iniciarEnvio(Long idPedido) {
        Pedido p = getPedidoOrThrow(idPedido);
        p.actualizarEstado(EstadoPedido.EN_PROCESO, p.getCarrito().getUser());
        Pedido saved = pedidoRepository.save(p);
        crearNotificacion(p.getCarrito().getUser(), saved, "EN_PROCESO");
        return pedidoMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PedidoResponse marcarEntregado(Long idPedido) {
        Pedido p = getPedidoOrThrow(idPedido);
        p.actualizarEstado(EstadoPedido.ENTREGADO, p.getCarrito().getUser());
        p.setFechaFinal(LocalDateTime.now());
        Pedido saved = pedidoRepository.save(p);
        crearNotificacion(p.getCarrito().getUser(), saved, "ENTREGADO");
        return pedidoMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PedidoResponse cancelar(Long idPedido, String motivo) {
        Pedido p = getPedidoOrThrow(idPedido);
        p.actualizarEstado(EstadoPedido.CANCELADO, p.getCarrito().getUser());
        p.setFechaFinal(LocalDateTime.now());
        Pedido saved = pedidoRepository.save(p);
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

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponse> findAll() {
        return pedidoRepository.findAll().stream()
                .map(pedidoMapper::toResponse)
                .collect(Collectors.toList());
    }

    private Pedido getPedidoOrThrow(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado ID: " + id));
    }

    private void crearNotificacion(User user, Pedido pedido, String estadoStr) {
        Notificacion n = new Notificacion();
        n.setUser(user);
        n.setMensaje("Estado del pedido #" + pedido.getIdPedido() + " actualizado a: " + estadoStr);
        n.setFechaEnvio(LocalDateTime.now());
        n.setEstado(EstadoNotificacion.ENVIADA);
        n.setPedido(pedido);
        notificacionRepository.save(n);
    }

    private void crearNotificacionCupon(User user, Pedido pedido, String codigoCupon) {
        Notificacion n = new Notificacion();
        n.setUser(user);
        n.setMensaje("¡Felicidades! Has alcanzado 30 puntos. Usa el cupón " + codigoCupon + " para un descuento de 15% en tu siguiente compra.");
        n.setFechaEnvio(LocalDateTime.now());
        n.setEstado(EstadoNotificacion.ENVIADA);
        n.setPedido(pedido);
        notificacionRepository.save(n);
    }
}

