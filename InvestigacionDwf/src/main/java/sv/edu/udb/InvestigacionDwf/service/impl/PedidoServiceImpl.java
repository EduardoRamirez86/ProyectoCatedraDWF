package sv.edu.udb.InvestigacionDwf.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import sv.edu.udb.InvestigacionDwf.security.jwt.CustomUserDetails;
import sv.edu.udb.InvestigacionDwf.service.CuponService;
import sv.edu.udb.InvestigacionDwf.service.ParametroService;
import sv.edu.udb.InvestigacionDwf.service.PedidoService;
import sv.edu.udb.InvestigacionDwf.service.assembler.PedidoAssembler;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private static final Logger logger = LoggerFactory.getLogger(PedidoServiceImpl.class);

    private final PedidoRepository pedidoRepository;
    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final DireccionRepository direccionRepository;
    private final UserRepository userRepository;
    private final NotificacionRepository notificacionRepository; // Mantener si la usas directamente aquí
    private final CuponService cuponService;
    private final ParametroService parametroService;
    private final CuponRepository cuponRepository;
    private final PedidoAssembler pedidoAssembler;
    private final PagedResourcesAssembler<Pedido> pagedResourcesAssembler;


    @Override
    @Transactional
    public PedidoResponse checkout(PedidoRequest req) {
        // --- Validaciones iniciales del Request ---
        if (Objects.isNull(req)) {
            logger.error("Solicitud de pedido nula.");
            throw new IllegalArgumentException("La solicitud de pedido no puede ser nula.");
        }
        if (Objects.isNull(req.getIdCarrito())) {
            logger.error("ID de carrito nulo en la solicitud de pedido.");
            throw new IllegalArgumentException("El ID del carrito es obligatorio para realizar un pedido.");
        }
        if (Objects.isNull(req.getTipoPago())) {
            logger.error("Tipo de pago nulo en la solicitud de pedido.");
            throw new IllegalArgumentException("La forma de pago es obligatoria.");
        }

        // --- Verificación del Contexto de Seguridad para el Carrito ---
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication) || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            logger.warn("Intento de checkout sin autenticación válida.");
            throw new SecurityException("Acceso denegado: Se requiere autenticación válida.");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // Obtiene el carrito por ID o lanza una excepción si no existe
        Carrito carrito = carritoRepository.findById(req.getIdCarrito())
                .orElseThrow(() -> {
                    logger.warn("Carrito no encontrado con ID: {}", req.getIdCarrito());
                    return new ResourceNotFoundException("Carrito no encontrado con ID: " + req.getIdCarrito());
                });

        // Asegurar que el carrito pertenece al usuario autenticado
        if (Objects.isNull(carrito.getUser()) || !carrito.getUser().getIdUser().equals(userDetails.getUserId())) {
            logger.warn("Acceso denegado: Usuario {} intentó checkout con carrito de otro usuario (ID Carrito: {}).", userDetails.getUserId(), req.getIdCarrito());
            throw new SecurityException("Acceso denegado: No puedes realizar un pedido con el carrito de otro usuario.");
        }

        // Obtiene los ítems del carrito
        List<CarritoItem> items = carritoItemRepository.findByCarrito_IdCarrito(carrito.getIdCarrito());
        if (items.isEmpty()) {
            logger.warn("Intento de checkout con carrito vacío para el usuario ID: {}", userDetails.getUserId());
            throw new IllegalStateException("No se puede realizar la compra con el carrito vacío.");
        }

        // Calcula el subtotal sumando el precio de cada ítem multiplicado por su cantidad
        BigDecimal subtotal = items.stream()
                .map(i -> {
                    if (Objects.isNull(i.getProducto()) || Objects.isNull(i.getProducto().getPrecio())) {
                        logger.error("Ítem de carrito con producto o precio nulo. Ítem ID: {}. Carrito ID: {}", i.getIdCarritoItem(), carrito.getIdCarrito());
                        throw new IllegalStateException("Uno de los productos en el carrito no tiene precio o es nulo.");
                    }
                    return i.getProducto().getPrecio().multiply(BigDecimal.valueOf(i.getCantidad()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        User user = carrito.getUser(); // El usuario ya se obtuvo del carrito y está validado por la seguridad

        // --- Aplicación de Cupón ---
        if (Objects.nonNull(req.getCuponCodigo()) && !req.getCuponCodigo().isBlank()) {
            if (cuponService.validateCoupon(req.getCuponCodigo(), user)) {
                Cupon cupon = cuponRepository.findByCodigo(req.getCuponCodigo())
                        .orElseThrow(() -> {
                            logger.warn("Cupón no encontrado con código: {}", req.getCuponCodigo());
                            return new ResourceNotFoundException("Cupón no encontrado: " + req.getCuponCodigo());
                        });
                // Calcula el descuento basado en el porcentaje del cupón
                BigDecimal descuento = subtotal.multiply(
                        BigDecimal.valueOf(cupon.getPorcentajeDescuento()).divide(BigDecimal.valueOf(100), BigDecimal.ROUND_HALF_EVEN) // Usar un RoundingMode
                );
                subtotal = subtotal.subtract(descuento);
                logger.info("Cupón '{}' aplicado para usuario {}. Descuento: {}", req.getCuponCodigo(), user.getUsername(), descuento);
                // Redime el cupón para que no se use nuevamente
                cuponService.redeemCoupon(req.getCuponCodigo(), user);
            } else {
                logger.warn("Cupón inválido o no aplicable '{}' para usuario {}.", req.getCuponCodigo(), user.getUsername());
                throw new IllegalStateException("Cupón inválido o no aplicable.");
            }
        }

        // --- Cálculo de Costo de Envío y Total Final ---
        BigDecimal shippingCost = parametroService
                .obtenerBigDecimal("costo_envio", new BigDecimal("5.00")); // Valor por defecto en caso de no encontrarlo
        BigDecimal total = subtotal.add(shippingCost);
        logger.info("Calculando total para usuario {}: Subtotal={}, CostoEnvio={}, Total={}", user.getUsername(), subtotal, shippingCost, total);

        // --- Construcción del Pedido ---
        Pedido pedido = Pedido.builder()
                .carrito(carrito)
                .fechaInicio(LocalDateTime.now())
                .total(total)
                .tipoPago(req.getTipoPago())
                .build();

        // Actualiza el estado del pedido a PENDIENTE (asumiendo que inicia en este estado)
        pedido.actualizarEstado(EstadoPedido.PENDIENTE, user);

        // --- Verificación y Asignación de Dirección de Entrega ---
        if (Objects.isNull(req.getIdDireccion())) {
            logger.error("ID de dirección de entrega nulo en la solicitud de pedido.");
            throw new IllegalArgumentException("Debe especificar una dirección de entrega.");
        }
        Direccion dir = direccionRepository.findById(req.getIdDireccion())
                .orElseThrow(() -> {
                    logger.warn("Dirección no encontrada con ID: {}", req.getIdDireccion());
                    return new ResourceNotFoundException("Dirección no encontrada con ID: " + req.getIdDireccion());
                });
        // Asegurar que la dirección pertenece al usuario autenticado
        if (Objects.isNull(dir.getUser()) || !dir.getUser().getIdUser().equals(userDetails.getUserId())) {
            logger.warn("Acceso denegado: Usuario {} intentó usar una dirección que no le pertenece (ID Dirección: {}).", userDetails.getUserId(), req.getIdDireccion());
            throw new SecurityException("Acceso denegado: La dirección proporcionada no pertenece al usuario autenticado.");
        }
        pedido.setDireccion(dir);

        // --- Cálculo y Asignación de Puntos Ganados ---
        int puntosGanados = items.stream()
                .mapToInt(i -> {
                    if (Objects.isNull(i.getProducto())) {
                        logger.error("Ítem de carrito con producto nulo al calcular puntos. Ítem ID: {}", i.getIdCarritoItem());
                        return 0; // O lanzar excepción si es un error fatal, dependiendo de la política de negocio
                    }
                    return i.getProducto().getCantidadPuntos() * i.getCantidad();
                })
                .sum();
        pedido.setPuntosTotales(puntosGanados);
        logger.info("Puntos ganados por el pedido para usuario {}: {}", user.getUsername(), puntosGanados);

        // --- Actualización de Puntos del Usuario ---
        int puntosAntes = user.getPuntos();
        int puntosNuevos = puntosAntes + puntosGanados;
        user.setPuntos(puntosNuevos);

        // Registra el historial de puntos con el Builder de Lombok
        var hp = HistorialPuntos.builder()
                .pedido(pedido)
                .user(user)
                .fecha(LocalDateTime.now())
                .cantidadAnterior(puntosAntes)
                .cantidadNueva(puntosNuevos)
                .build();
        // Asegúrate de que HistorialPuntos sea una lista inicializada en Pedido
        if (Objects.isNull(pedido.getHistorialPuntos())) {
            pedido.setHistorialPuntos(new java.util.ArrayList<>());
        }
        pedido.getHistorialPuntos().add(hp);

        // Guarda el pedido y actualiza el usuario
        var savedPedido = pedidoRepository.save(pedido); // Guarda el pedido y sus historial de puntos
        userRepository.save(user); // Guarda el usuario con los puntos actualizados

        // Si el usuario alcanza 30 puntos, genera un cupón y deduce los puntos
        if (user.getPuntos() >= 30) { // Usamos user.getPuntos() que ya está actualizado
            logger.info("Usuario {} alcanzó {} puntos. Generando cupón y deduciendo puntos.", user.getUsername(), user.getPuntos());
            var nuevoCupon = cuponService.generateCouponForUser(user);
            int puntosAcumuladosAntesDeduccion = user.getPuntos(); // Puntos antes de deducir 30
            int puntosFinalesDespuesDeduccion = puntosAcumuladosAntesDeduccion - 30;
            user.setPuntos(puntosFinalesDespuesDeduccion);
            // Registra el historial de deducción de puntos con el Builder de Lombok
            var hp2 = HistorialPuntos.builder()
                    .pedido(savedPedido)
                    .user(user)
                    .fecha(LocalDateTime.now())
                    .cantidadAnterior(puntosAcumuladosAntesDeduccion) // Los puntos antes de esta deducción
                    .cantidadNueva(user.getPuntos()) // Los puntos después de esta deducción
                    .build();
            savedPedido.getHistorialPuntos().add(hp2);
            userRepository.save(user); // Guarda el usuario con los puntos deducidos
            crearNotificacionCupon(user, savedPedido, nuevoCupon.getCodigo(), nuevoCupon.getPorcentajeDescuento());
            logger.info("Cupón '{}' generado para usuario {} por redención de puntos.", nuevoCupon.getCodigo(), user.getUsername());
        }

        // Crea una notificación para el usuario sobre el estado del pedido
        crearNotificacion(user, savedPedido, "PAGADO");
        logger.info("Pedido {} procesado exitosamente para el usuario {}.", savedPedido.getIdPedido(), user.getUsername());


        // --- LIMPIEZA DEL CARRITO DESPUÉS DE UN CHECKOUT EXITOSO ---
        // Esta operación se realiza al final de la transacción para asegurar que
        // si todo lo demás fue exitoso, el carrito sea vaciado.
        try {
            // Eliminar todos los CarritoItems asociados a este carrito
            carritoItemRepository.deleteByCarrito(carrito); // Requiere el método deleteByCarrito en CarritoItemRepository
            logger.info("Carrito del usuario {} (ID: {}) limpiado exitosamente después del pedido {}.", user.getUsername(), carrito.getIdCarrito(), savedPedido.getIdPedido());
        } catch (Exception e) {
            // Si la limpieza del carrito falla, es un problema secundario pero importante.
            // La transacción ya está por cometerse, por lo que el pedido ya se guardó.
            // Registra el error y lanza una excepción para asegurar que la transacción haga rollback
            // si la limpieza es CRÍTICA y no "best-effort" (es decir, el pedido no debería existir si el carrito no se vacía).
            logger.error("Error crítico al limpiar el carrito del usuario {} (ID: {}) después del pedido {}. Causa: {}",
                    user.getUsername(), carrito.getIdCarrito(), savedPedido.getIdPedido(), e.getMessage(), e);
            throw new IllegalStateException("El pedido se ha creado, pero ocurrió un error crítico al limpiar el carrito. Revise los logs.", e);
        }
        // --- FIN LIMPIEZA DEL CARRITO ---

        // Convierte el pedido guardado a un modelo HAL
        return pedidoAssembler.toModel(savedPedido);
    }

    @Override
    @Transactional
    public PedidoResponse confirmar(Long idPedido) {
        // Validación de nulidad para el ID
        if (Objects.isNull(idPedido)) {
            logger.error("ID de pedido nulo para confirmar.");
            throw new IllegalArgumentException("El ID del pedido no puede ser nulo para confirmar.");
        }
        var p = getPedidoOrThrow(idPedido);
        User user = p.getCarrito().getUser();
        if (Objects.isNull(user)) {
            logger.error("Usuario asociado al carrito del pedido {} es nulo al intentar confirmar.", idPedido);
            throw new IllegalStateException("El pedido no tiene un usuario asociado válido.");
        }
        p.actualizarEstado(EstadoPedido.PAGADO, user); // Se asume que confirmar es sinónimo de pagado en algunos flujos
        p.setFechaFinal(LocalDateTime.now());
        return saveAndNotify(p, "PAGADO");
    }

    @Override
    @Transactional
    public PedidoResponse pagar(Long idPedido, PagoRequest pagoReq) {
        // Validación para asegurar que el pago tiene un usuario asociado
        if (Objects.isNull(pagoReq) || Objects.isNull(pagoReq.getUsuario())) {
            logger.error("Solicitud de pago o usuario nulo al intentar pagar.");
            throw new IllegalArgumentException("La solicitud de pago o el usuario asociado no pueden ser nulos.");
        }
        if (Objects.isNull(idPedido)) {
            logger.error("ID de pedido nulo al intentar pagar.");
            throw new IllegalArgumentException("El ID del pedido no puede ser nulo para pagar.");
        }

        var p = getPedidoOrThrow(idPedido);
        // Asegurar que el usuario del pago coincide con el dueño del pedido
        if (!p.getCarrito().getUser().getIdUser().equals(pagoReq.getUsuario().getIdUser())) {
            logger.warn("Intento de pagar el pedido {} con un usuario diferente (ID: {}) al dueño (ID: {}).", idPedido, pagoReq.getUsuario().getIdUser(), p.getCarrito().getUser().getIdUser());
            throw new SecurityException("Acceso denegado: No puedes pagar el pedido de otro usuario.");
        }
        p.actualizarEstado(EstadoPedido.PAGADO, pagoReq.getUsuario());
        return saveAndNotify(p, "PAGADO");
    }

    @Override
    @Transactional
    public PedidoResponse iniciarEnvio(Long idPedido) {
        // Validación de nulidad para el ID
        if (Objects.isNull(idPedido)) {
            logger.error("ID de pedido nulo al intentar iniciar envío.");
            throw new IllegalArgumentException("El ID del pedido no puede ser nulo para iniciar envío.");
        }
        var p = getPedidoOrThrow(idPedido);
        User user = p.getCarrito().getUser();
        if (Objects.isNull(user)) {
            logger.error("Usuario asociado al carrito del pedido {} es nulo al intentar iniciar envío.", idPedido);
            throw new IllegalStateException("El pedido no tiene un usuario asociado válido.");
        }
        p.actualizarEstado(EstadoPedido.EN_PROCESO, user);
        return saveAndNotify(p, "EN_PROCESO");
    }

    @Override
    @Transactional
    public PedidoResponse marcarEntregado(Long idPedido) {
        // Validación de nulidad para el ID
        if (Objects.isNull(idPedido)) {
            logger.error("ID de pedido nulo al intentar marcar como entregado.");
            throw new IllegalArgumentException("El ID del pedido no puede ser nulo para marcar como entregado.");
        }
        var p = getPedidoOrThrow(idPedido);
        User user = p.getCarrito().getUser();
        if (Objects.isNull(user)) {
            logger.error("Usuario asociado al carrito del pedido {} es nulo al intentar marcar como entregado.", idPedido);
            throw new IllegalStateException("El pedido no tiene un usuario asociado válido.");
        }
        p.actualizarEstado(EstadoPedido.ENTREGADO, user);
        p.setFechaFinal(LocalDateTime.now());
        return saveAndNotify(p, "ENTREGADO");
    }

    @Override
    @Transactional
    public PedidoResponse cancelar(Long idPedido, String motivo) {
        // Validar que el motivo no sea nulo/vacío
        if (Objects.isNull(motivo) || motivo.isBlank()) {
            logger.error("Motivo de cancelación nulo o vacío.");
            throw new IllegalArgumentException("El motivo de cancelación no puede estar vacío.");
        }
        if (Objects.isNull(idPedido)) {
            logger.error("ID de pedido nulo al intentar cancelar.");
            throw new IllegalArgumentException("El ID del pedido no puede ser nulo para cancelar.");
        }

        var p = getPedidoOrThrow(idPedido);
        User user = p.getCarrito().getUser();
        if (Objects.isNull(user)) {
            logger.error("Usuario asociado al carrito del pedido {} es nulo al intentar cancelar.", idPedido);
            throw new IllegalStateException("El pedido no tiene un usuario asociado válido.");
        }
        p.actualizarEstado(EstadoPedido.CANCELADO, user);
        p.setFechaFinal(LocalDateTime.now());
        return saveAndNotify(p, "CANCELADO");
    }

    @Override
    @Transactional(readOnly = true)
    public PagedModel<PedidoResponse> findAllByUser(Long idUser, Pageable pageable) {
        // Validación de nulidad para el ID de usuario
        if (Objects.isNull(idUser)) {
            logger.error("ID de usuario nulo para buscar pedidos por usuario.");
            throw new IllegalArgumentException("El ID de usuario no puede ser nulo.");
        }
        if (Objects.isNull(pageable)) {
            logger.error("Objeto Pageable nulo para buscar pedidos por usuario.");
            throw new IllegalArgumentException("La paginación no puede ser nula.");
        }
        Page<Pedido> page = pedidoRepository.findByCarrito_User_IdUser(idUser, pageable);
        return pagedResourcesAssembler.toModel(page, pedidoAssembler);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedModel<PedidoResponse> findAll(Pageable pageable) {
        // Considera validar 'pageable' si es una dependencia externa que podría ser nula
        if (Objects.isNull(pageable)) {
            logger.error("Objeto Pageable nulo para buscar todos los pedidos.");
            throw new IllegalArgumentException("La paginación no puede ser nula.");
        }
        Page<Pedido> page = pedidoRepository.findAll(pageable);
        return pagedResourcesAssembler.toModel(page, pedidoAssembler);
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoResponse getById(Long id) {
        // Validación de nulidad para el ID
        if (Objects.isNull(id)) {
            logger.error("ID de pedido nulo para obtener por ID.");
            throw new IllegalArgumentException("El ID del pedido no puede ser nulo.");
        }
        return pedidoAssembler.toModel(getPedidoOrThrow(id));
    }

    // --- Métodos auxiliares ---

    private PedidoResponse saveAndNotify(Pedido p, String estado) {
        // Se podría validar 'p' y 'estado' aquí si se llaman desde otros lugares
        if (Objects.isNull(p) || Objects.isNull(estado) || estado.isBlank()) {
            logger.error("Parámetros nulos o vacíos al guardar y notificar pedido. Pedido: {}, Estado: {}", p, estado);
            throw new IllegalArgumentException("El pedido o el estado no pueden ser nulos/vacíos.");
        }
        var saved = pedidoRepository.save(p);
        // LLAMADA A LOS MÉTODOS PRIVADOS DE NOTIFICACIÓN EN ESTA MISMA CLASE
        crearNotificacion(p.getCarrito().getUser(), saved, estado);
        return pedidoAssembler.toModel(saved);
    }

    private Pedido getPedidoOrThrow(Long id) {
        // Validación explícita de nulidad para el ID antes de la operación del repositorio
        if (Objects.isNull(id)) {
            logger.error("ID de pedido nulo al intentar obtener un pedido.");
            throw new IllegalArgumentException("El ID del pedido no puede ser nulo.");
        }
        return pedidoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Pedido no encontrado con ID: {}", id);
                    return new ResourceNotFoundException("Pedido no encontrado con ID: " + id);
                });
    }

    /**
     * Crea una notificación para el usuario sobre el estado de su pedido.
     * Este método es privado y vive dentro de PedidoServiceImpl.
     *
     * @param user El usuario al que se le enviará la notificación.
     * @param pedido El pedido relacionado con la notificación.
     * @param estadoStr La cadena de texto del estado del pedido (ej. "PAGADO", "EN_PROCESO").
     * @throws IllegalArgumentException Si algún parámetro esencial es nulo o vacío.
     */
    private void crearNotificacion(User user, Pedido pedido, String estadoStr) {
        // Validaciones de nulidad para los parámetros de la notificación
        if (Objects.isNull(user) || Objects.isNull(pedido) || Objects.isNull(estadoStr) || estadoStr.isBlank()) {
            logger.error("Parámetros de notificación nulos o vacíos. Usuario: {}, Pedido: {}, Estado: {}", user, pedido, estadoStr);
            throw new IllegalArgumentException("Los parámetros de la notificación no pueden ser nulos/vacíos.");
        }

        // Construcción de Notificación con Builder de Lombok
        var n = Notificacion.builder()
                .user(user)
                .mensaje("Estado del pedido #" + pedido.getIdPedido() + " actualizado a: " + estadoStr)
                .fechaEnvio(LocalDateTime.now())
                .estado(EstadoNotificacion.ENVIADA)
                .pedido(pedido)
                .build();
        notificacionRepository.save(n);
        logger.info("Notificación creada para usuario {} sobre pedido {}: {}", user.getUsername(), pedido.getIdPedido(), estadoStr);
    }

    /**
     * Crea una notificación para el usuario cuando ha ganado un cupón por puntos.
     * Este método es privado y vive dentro de PedidoServiceImpl.
     *
     * @param user El usuario al que se le enviará la notificación del cupón.
     * @param pedido El pedido que generó la obtención de puntos (opcional, pero útil para referencia).
     * @param codigoCupon El código del cupón generado.
     * @param porcentajeDescuento El porcentaje de descuento del cupón.
     * @throws IllegalArgumentException Si algún parámetro esencial es nulo o vacío.
     */
    private void crearNotificacionCupon(User user, Pedido pedido, String codigoCupon, double porcentajeDescuento) {
        // Validaciones de nulidad para los parámetros de la notificación de cupón
        if (Objects.isNull(user) || Objects.isNull(pedido) || Objects.isNull(codigoCupon) || codigoCupon.isBlank()) {
            logger.error("Parámetros de notificación de cupón nulos o vacíos. Usuario: {}, Pedido: {}, Código: {}", user, pedido, codigoCupon);
            throw new IllegalArgumentException("Los parámetros de la notificación de cupón no pueden ser nulos/vacíos.");
        }

        // Construcción de Notificación con Builder de Lombok
        var n = Notificacion.builder()
                .user(user)
                .mensaje("¡Felicidades! Has alcanzado 30 puntos. Usa el cupón " + codigoCupon + " para un " + porcentajeDescuento + "% de descuento.")
                .fechaEnvio(LocalDateTime.now())
                .estado(EstadoNotificacion.ENVIADA)
                .pedido(pedido)
                .build();
        notificacionRepository.save(n);
        logger.info("Notificación de cupón creada para usuario {}: Código {}", user.getUsername(), codigoCupon);
    }
}