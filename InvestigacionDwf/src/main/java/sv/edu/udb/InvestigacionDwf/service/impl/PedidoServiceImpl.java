// src/main/java/sv/edu/udb/InvestigacionDwf/service/impl/PedidoServiceImpl.java
package sv.edu.udb.InvestigacionDwf.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
import sv.edu.udb.InvestigacionDwf.service.PedidoService;
import sv.edu.udb.InvestigacionDwf.service.mapper.PedidoMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final DireccionRepository direccionRepository;
    private final PedidoMapper pedidoMapper;
    private final UserRepository userRepository;
    private final NotificacionRepository notificacionRepository;
    private final CuponService cuponService;
    private final CuponRepository cuponRepository;

    /**
     * Inyecta desde application.properties el costo de envío
     */
    @Value("${app.shipping.cost}")
    private BigDecimal shippingCost;

    @Override
    @Transactional
    public PedidoResponse checkout(PedidoRequest req) {
        var carrito = carritoRepository.findById(req.getIdCarrito())
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado ID: " + req.getIdCarrito()));

        var items = carritoItemRepository.findByCarrito_IdCarrito(carrito.getIdCarrito());
        if (items.isEmpty()) {
            throw new IllegalStateException("No se puede realizar la compra con el carrito vacío.");
        }

        // 1) Subtotal productos
        BigDecimal subtotal = items.stream()
                .map(i -> i.getProducto().getPrecio()
                        .multiply(BigDecimal.valueOf(i.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2) Aplicar cupón si hay
        User user = carrito.getUser();
        if (Objects.nonNull(req.getCuponCodigo()) && !req.getCuponCodigo().isBlank()) {
            if (cuponService.validateCoupon(req.getCuponCodigo(), user)) {
                var cupon = cuponRepository.findByCodigo(req.getCuponCodigo())
                        .orElseThrow(() -> new ResourceNotFoundException("Cupón no encontrado: " + req.getCuponCodigo()));
                BigDecimal descuento = subtotal.multiply(
                        BigDecimal.valueOf(cupon.getPorcentajeDescuento()).divide(BigDecimal.valueOf(100))
                );
                subtotal = subtotal.subtract(descuento);
                cuponService.redeemCoupon(req.getCuponCodigo(), user);
            } else {
                throw new IllegalStateException("Cupón inválido o no aplicable");
            }
        }

        // 3) Sumar envío (ahora configurable)
        BigDecimal total = subtotal.add(shippingCost);

        // 4) Construir pedido con builder
        var pedido = Pedido.builder()
                .carrito(carrito)
                .fechaInicio(LocalDateTime.now())
                .total(total)
                .tipoPago(req.getTipoPago())
                .build();

        pedido.actualizarEstado(EstadoPedido.PENDIENTE, user);

        // 5) Dirección
        if (Objects.isNull(req.getIdDireccion())) {
            throw new IllegalArgumentException("Debe especificar una dirección de entrega.");
        }
        var dir = direccionRepository.findById(req.getIdDireccion())
                .orElseThrow(() -> new ResourceNotFoundException("Dirección no encontrada ID: " + req.getIdDireccion()));
        pedido.setDireccion(dir);

        // 6) Puntos
        int puntosGanados = items.stream()
                .mapToInt(i -> i.getProducto().getCantidadPuntos() * i.getCantidad())
                .sum();
        pedido.setPuntosTotales(puntosGanados);

        int puntosAntes = user.getPuntos();
        int puntosNuevos = puntosAntes + puntosGanados;
        user.setPuntos(puntosNuevos);

        var hp = HistorialPuntos.builder()
                .pedido(pedido)
                .user(user)
                .fecha(LocalDateTime.now())
                .cantidadAnterior(puntosAntes)
                .cantidadNueva(puntosNuevos)
                .build();
        pedido.getHistorialPuntos().add(hp);

        // 7) Guardar
        var saved = pedidoRepository.save(pedido);
        userRepository.save(user);

        // 8) Cupón por puntos si aplica
        if (puntosNuevos >= 30) {
            var nuevoCupon = cuponService.generateCouponForUser(user);
            user.setPuntos(puntosNuevos - 30);
            var hp2 = HistorialPuntos.builder()
                    .pedido(saved)
                    .user(user)
                    .fecha(LocalDateTime.now())
                    .cantidadAnterior(puntosNuevos)
                    .cantidadNueva(user.getPuntos())
                    .build();
            saved.getHistorialPuntos().add(hp2);
            userRepository.save(user);
            crearNotificacionCupon(user, saved, nuevoCupon.getCodigo());
        }

        crearNotificacion(user, saved, "PAGADO");
        return pedidoMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PedidoResponse confirmar(Long idPedido) {
        var p = getPedidoOrThrow(idPedido);
        p.actualizarEstado(EstadoPedido.PAGADO, p.getCarrito().getUser());
        p.setFechaFinal(LocalDateTime.now());
        return saveAndNotify(p, "PAGADO");
    }

    @Override
    @Transactional
    public PedidoResponse pagar(Long idPedido, PagoRequest pagoReq) {
        var p = getPedidoOrThrow(idPedido);
        p.actualizarEstado(EstadoPedido.PAGADO, pagoReq.getUsuario());
        return saveAndNotify(p, "PAGADO");
    }

    @Override
    @Transactional
    public PedidoResponse iniciarEnvio(Long idPedido) {
        var p = getPedidoOrThrow(idPedido);
        p.actualizarEstado(EstadoPedido.EN_PROCESO, p.getCarrito().getUser());
        return saveAndNotify(p, "EN_PROCESO");
    }

    @Override
    @Transactional
    public PedidoResponse marcarEntregado(Long idPedido) {
        var p = getPedidoOrThrow(idPedido);
        p.actualizarEstado(EstadoPedido.ENTREGADO, p.getCarrito().getUser());
        p.setFechaFinal(LocalDateTime.now());
        return saveAndNotify(p, "ENTREGADO");
    }

    @Override
    @Transactional
    public PedidoResponse cancelar(Long idPedido, String motivo) {
        var p = getPedidoOrThrow(idPedido);
        p.actualizarEstado(EstadoPedido.CANCELADO, p.getCarrito().getUser());
        p.setFechaFinal(LocalDateTime.now());
        return saveAndNotify(p, "CANCELADO");
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

    // Helpers

    private PedidoResponse saveAndNotify(Pedido p, String estado) {
        var saved = pedidoRepository.save(p);
        crearNotificacion(p.getCarrito().getUser(), saved, estado);
        return pedidoMapper.toResponse(saved);
    }

    private Pedido getPedidoOrThrow(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado ID: " + id));
    }

    private void crearNotificacion(User user, Pedido pedido, String estadoStr) {
        var n = Notificacion.builder()
                .user(user)
                .mensaje("Estado del pedido #" + pedido.getIdPedido() + " actualizado a: " + estadoStr)
                .fechaEnvio(LocalDateTime.now())
                .estado(EstadoNotificacion.ENVIADA)
                .pedido(pedido)
                .build();
        notificacionRepository.save(n);
    }

    private void crearNotificacionCupon(User user, Pedido pedido, String codigoCupon) {
        var n = Notificacion.builder()
                .user(user)
                .mensaje("¡Felicidades! Has alcanzado 30 puntos. Usa el cupón " + codigoCupon + " para un 15% de descuento.")
                .fechaEnvio(LocalDateTime.now())
                .estado(EstadoNotificacion.ENVIADA)
                .pedido(pedido)
                .build();
        notificacionRepository.save(n);
    }
}


