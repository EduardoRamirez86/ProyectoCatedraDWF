// src/main/java/sv/edu/udb/InvestigacionDwf/service/impl/PedidoServiceImpl.java
package sv.edu.udb.InvestigacionDwf.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
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
import sv.edu.udb.InvestigacionDwf.service.assembler.PedidoAssembler;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final DireccionRepository direccionRepository;
    private final UserRepository userRepository;
    private final NotificacionRepository notificacionRepository;
    private final CuponService cuponService;
    private final CuponRepository cuponRepository;
    private final PedidoAssembler pedidoAssembler;
    private final PagedResourcesAssembler<Pedido> pagedResourcesAssembler;


    @Value("${app.shipping.cost}")
    private BigDecimal shippingCost;

    @Override
    @Transactional
    public PedidoResponse checkout(PedidoRequest req) {
        // Obtiene el carrito por ID o lanza una excepción si no existe
        var carrito = carritoRepository.findById(req.getIdCarrito())
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado ID: " + req.getIdCarrito()));
        // Obtiene los items del carrito
        var items = carritoItemRepository.findByCarrito_IdCarrito(carrito.getIdCarrito());
        if (items.isEmpty()) {
            throw new IllegalStateException("No se puede realizar la compra con el carrito vacío.");
        }

        // Calcula el subtotal sumando el precio de cada item multiplicado por su cantidad
        BigDecimal subtotal = items.stream()
                .map(i -> i.getProducto().getPrecio().multiply(BigDecimal.valueOf(i.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        User user = carrito.getUser();
        // Verifica si se proporcionó un código de cupón y si es válido
        if (Objects.nonNull(req.getCuponCodigo()) && !req.getCuponCodigo().isBlank()) {
            if (cuponService.validateCoupon(req.getCuponCodigo(), user)) {
                var cupon = cuponRepository.findByCodigo(req.getCuponCodigo())
                        .orElseThrow(() -> new ResourceNotFoundException("Cupón no encontrado: " + req.getCuponCodigo()));
                // Calcula el descuento basado en el porcentaje del cupón
                BigDecimal descuento = subtotal.multiply(
                        BigDecimal.valueOf(cupon.getPorcentajeDescuento()).divide(BigDecimal.valueOf(100))
                );
                subtotal = subtotal.subtract(descuento);
                // Redime el cupón para que no se use nuevamente
                cuponService.redeemCoupon(req.getCuponCodigo(), user);
            } else {
                throw new IllegalStateException("Cupón inválido o no aplicable");
            }
        }

        // Agrega el costo de envío al subtotal para obtener el total
        BigDecimal total = subtotal.add(shippingCost);
        // Construye el objeto Pedido con los datos calculados
        var pedido = Pedido.builder()
                .carrito(carrito)
                .fechaInicio(LocalDateTime.now())
                .total(total)
                .tipoPago(req.getTipoPago())
                .build();
        // Actualiza el estado del pedido a PENDIENTE
        pedido.actualizarEstado(EstadoPedido.PENDIENTE, user);

        // Verifica que se haya proporcionado una dirección de entrega
        if (Objects.isNull(req.getIdDireccion())) {
            throw new IllegalArgumentException("Debe especificar una dirección de entrega.");
        }
        // Obtiene la dirección por ID o lanza una excepción si no existe
        var dir = direccionRepository.findById(req.getIdDireccion())
                .orElseThrow(() -> new ResourceNotFoundException("Dirección no encontrada ID: " + req.getIdDireccion()));
        pedido.setDireccion(dir);

        // Calcula los puntos ganados por los items del pedido
        int puntosGanados = items.stream()
                .mapToInt(i -> i.getProducto().getCantidadPuntos() * i.getCantidad())
                .sum();
        pedido.setPuntosTotales(puntosGanados);

        // Actualiza los puntos del usuario
        int puntosAntes = user.getPuntos();
        int puntosNuevos = puntosAntes + puntosGanados;
        user.setPuntos(puntosNuevos);

        // Registra el historial de puntos
        var hp = HistorialPuntos.builder()
                .pedido(pedido)
                .user(user)
                .fecha(LocalDateTime.now())
                .cantidadAnterior(puntosAntes)
                .cantidadNueva(puntosNuevos)
                .build();
        pedido.getHistorialPuntos().add(hp);

        // Guarda el pedido y actualiza el usuario
        var saved = pedidoRepository.save(pedido);
        userRepository.save(user);

        // Si el usuario alcanza 30 puntos, genera un cupón y deduce los puntos
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
            crearNotificacionCupon(user, saved, nuevoCupon.getCodigo(), nuevoCupon.getPorcentajeDescuento());
        }

        // Crea una notificación para el usuario sobre el estado del pedido
        crearNotificacion(user, saved, "PAGADO");
        // Convierte el pedido guardado a un modelo HAL
        return pedidoAssembler.toModel(saved);
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
    public PagedModel<PedidoResponse> findAllByUser(Long idUser, Pageable pageable) {
        Page<Pedido> page = pedidoRepository.findByCarrito_User_IdUser(idUser, pageable);
        return pagedResourcesAssembler.toModel(page, pedidoAssembler);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedModel<PedidoResponse> findAll(Pageable pageable) {
        Page<Pedido> page = pedidoRepository.findAll(pageable);
        return pagedResourcesAssembler.toModel(page, pedidoAssembler);
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoResponse getById(Long id) {
        return pedidoAssembler.toModel(getPedidoOrThrow(id));
    }

    // Métodos auxiliares

    private PedidoResponse saveAndNotify(Pedido p, String estado) {
        var saved = pedidoRepository.save(p);
        crearNotificacion(p.getCarrito().getUser(), saved, estado);
        return pedidoAssembler.toModel(saved);
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

    private void crearNotificacionCupon(User user, Pedido pedido, String codigoCupon, double porcentajeDescuento) {
        var n = Notificacion.builder()
                .user(user)
                .mensaje("¡Felicidades! Has alcanzado 30 puntos. Usa el cupón " + codigoCupon + " para un " + porcentajeDescuento + "% de descuento.")
                .fechaEnvio(LocalDateTime.now())
                .estado(EstadoNotificacion.ENVIADA)
                .pedido(pedido)
                .build();
        notificacionRepository.save(n);
    }
}