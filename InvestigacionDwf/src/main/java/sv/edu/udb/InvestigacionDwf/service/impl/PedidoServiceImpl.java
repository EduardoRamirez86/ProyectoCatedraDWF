package sv.edu.udb.InvestigacionDwf.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import java.math.RoundingMode; // Importar para redondeo
import java.time.LocalDate; // Importar para rangos de fecha del dashboard
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors; // Para procesar productos más vendidos

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private static final Logger logger = LoggerFactory.getLogger(PedidoServiceImpl.class);

    private final PedidoRepository pedidoRepository;
    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final DireccionRepository direccionRepository;
    private final UserRepository userRepository;
    private final NotificacionRepository notificacionRepository;
    private final CuponService cuponService;
    private final ParametroService parametroService;
    private final CuponRepository cuponRepository;
    private final ProductoRepository productoRepository; // <-- NECESARIO para actualizar stock
    private final RegistroGananciaRepository registroGananciaRepository;
    private final PedidoItemRepository pedidoItemRepository;// <-- NUEVO: Para registrar ganancias
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

        // --- LÓGICA DE STOCK Y GANANCIAS ---
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal gananciaBrutaEstimada = BigDecimal.ZERO; // Ganancia bruta antes de descuentos
        List<PedidoItemData> pedidoItemsData = new ArrayList<>(); // Lista temporal con datos para crear PedidoItems

        for (CarritoItem item : items) {
            Producto producto = item.getProducto();
            int cantidadComprada = item.getCantidad();

            if (Objects.isNull(producto) || Objects.isNull(producto.getPrecio()) || Objects.isNull(producto.getCosto()) || Objects.isNull(producto.getCantidad())) {
                logger.error("Producto con datos incompletos en el carrito. Ítem ID: {}", item.getIdCarritoItem());
                throw new IllegalStateException("Uno de los productos en el carrito tiene datos incompletos (precio, costo o stock nulo).");
            }

            // 1. Reducir stock del producto
            if (producto.getCantidad() < cantidadComprada) {
                logger.warn("Stock insuficiente para el producto '{}' (ID: {}). Cantidad disponible: {}, Cantidad solicitada: {}", producto.getNombre(), producto.getIdProducto(), producto.getCantidad(), cantidadComprada);
                throw new IllegalStateException("Stock insuficiente para el producto: " + producto.getNombre() + ". Solo quedan " + producto.getCantidad() + " unidades.");
            }
            producto.actualizarStock(cantidadComprada * -1); // Resta la cantidad
            productoRepository.save(producto); // Guarda el producto con el stock actualizado
            logger.info("Stock actualizado para producto '{}' (ID: {}). Nuevo stock: {}", producto.getNombre(), producto.getIdProducto(), producto.getCantidad());

            // 2. Calcular subtotal del pedido y ganancia bruta por item
            BigDecimal precioItem = producto.getPrecio().multiply(BigDecimal.valueOf(cantidadComprada));
            BigDecimal costoItem = producto.getCosto().multiply(BigDecimal.valueOf(cantidadComprada));
            BigDecimal gananciaItem = precioItem.subtract(costoItem);

            subtotal = subtotal.add(precioItem);
            gananciaBrutaEstimada = gananciaBrutaEstimada.add(gananciaItem);
            logger.debug("Item: {}, Precio: {}, Costo: {}, Ganancia: {}", producto.getNombre(), precioItem, costoItem, gananciaItem);

            // 3. Almacenar datos para crear PedidoItem después
            pedidoItemsData.add(new PedidoItemData(producto, cantidadComprada, producto.getPrecio()));
        }

        User user = carrito.getUser(); // El usuario ya se obtuvo del carrito y está validado por la seguridad
        BigDecimal descuentoAplicado = BigDecimal.ZERO;

        // --- Aplicación de Cupón ---
        if (Objects.nonNull(req.getCuponCodigo()) && !req.getCuponCodigo().isBlank()) {
            if (cuponService.validateCoupon(req.getCuponCodigo(), user)) {
                Cupon cupon = cuponRepository.findByCodigo(req.getCuponCodigo())
                        .orElseThrow(() -> {
                            logger.warn("Cupón no encontrado con código: {}", req.getCuponCodigo());
                            return new ResourceNotFoundException("Cupón no encontrado: " + req.getCuponCodigo());
                        });
                // Calcula el descuento basado en el porcentaje del cupón
                descuentoAplicado = subtotal.multiply(
                        BigDecimal.valueOf(cupon.getPorcentajeDescuento()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN) // Usar un RoundingMode
                );
                // Asegurarse de que el descuento no exceda el subtotal
                if (descuentoAplicado.compareTo(subtotal) > 0) {
                    descuentoAplicado = subtotal;
                }
                subtotal = subtotal.subtract(descuentoAplicado);
                logger.info("Cupón '{}' aplicado para usuario {}. Descuento: {}", req.getCuponCodigo(), user.getUsername(), descuentoAplicado);
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

        // Ajustar la ganancia total para reflejar el descuento aplicado
        // La ganancia neta es la ganancia bruta menos el descuento, ya que el descuento reduce el ingreso.
        BigDecimal gananciaNeta = gananciaBrutaEstimada.subtract(descuentoAplicado);
        logger.info("Ganancia neta calculada para el pedido del usuario {}: {}", user.getUsername(), gananciaNeta);

        // --- Construcción del Pedido SIN PedidoItems inicialmente ---
        Pedido pedido = Pedido.builder()
                .carrito(carrito)
                .fechaInicio(LocalDateTime.now())
                .subtotal(subtotal.add(descuentoAplicado))
                .descuentoAplicado(descuentoAplicado)
                .total(total)
                .gananciaEstimada(gananciaNeta)
                .tipoPago(req.getTipoPago())
                .pedidoItems(new ArrayList<>()) // Inicializa la lista vacía
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

        // PASO 1: Guarda el pedido PRIMERO (sin PedidoItems)
        var savedPedido = pedidoRepository.save(pedido);

        // PASO 2: AHORA crea y asigna los PedidoItems con la referencia al pedido guardado
        List<PedidoItem> pedidoItems = new ArrayList<>();
        for (PedidoItemData data : pedidoItemsData) {
            PedidoItem pedidoItem = PedidoItem.builder()
                    .pedido(savedPedido) // Asigna la referencia al pedido guardado
                    .producto(data.getProducto())
                    .cantidad(data.getCantidad())
                    .precioUnitario(data.getPrecioUnitario())
                    .build();
            pedidoItems.add(pedidoItem);
        }

        // PASO 3: Asigna los PedidoItems al pedido y guarda nuevamente
        savedPedido.setPedidoItems(pedidoItems);
        savedPedido = pedidoRepository.save(savedPedido);

        userRepository.save(user); // Guarda el usuario con los puntos actualizados

        // --- REGISTRO DE GANANCIA POR VENTA ---
        // Se crea un registro de ganancia una vez que el pedido es guardado exitosamente.
        var registroGanancia = RegistroGanancia.builder()
                .pedido(savedPedido)
                .fechaRegistro(LocalDateTime.now())
                .montoGanancia(gananciaNeta) // La ganancia neta ya considera el descuento
                .build();
        registroGananciaRepository.save(registroGanancia);
        logger.info("Registro de ganancia creado para el pedido ID {}: Ganancia {}", savedPedido.getIdPedido(), gananciaNeta);

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
            // Dependiendo de la política de negocio, podrías relanzar un IllegalStateException o simplemente logear.
            // Para asegurar la consistencia si la limpieza del carrito es vital, relanzamos:
            throw new IllegalStateException("El pedido se ha creado, pero ocurrió un error crítico al limpiar el carrito. Revise los logs.", e);
        }
        // --- FIN LIMPIEZA DEL CARRITO ---

        // Convierte el pedido guardado a un modelo HAL
        return pedidoAssembler.toModel(savedPedido);
    }

    // Clase auxiliar para almacenar datos temporalmente
    private static class PedidoItemData {
        private final Producto producto;
        private final int cantidad;
        private final BigDecimal precioUnitario;

        public PedidoItemData(Producto producto, int cantidad, BigDecimal precioUnitario) {
            this.producto = producto;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
        }

        public Producto getProducto() { return producto; }
        public int getCantidad() { return cantidad; }
        public BigDecimal getPrecioUnitario() { return precioUnitario; }
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

    // En PedidoServiceImpl.java

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getGananciasTotales() {
        return pedidoRepository.sumGananciaEstimadaByEstado(EstadoPedido.ENTREGADO).orElse(BigDecimal.ZERO);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getGananciasPorPeriodo(LocalDate fechaInicio, LocalDate fechaFin) {
        // Es CRÍTICO asegurarse de que fechaInicio y fechaFin se conviertan correctamente a LocalDateTime
        // para que coincidan con el tipo de dato en la entidad Pedido (fechaInicio)
        LocalDateTime startOfDay = fechaInicio.atStartOfDay();
        LocalDateTime endOfDay = fechaFin.atTime(23, 59, 59, 999999999); // Final del día

        return pedidoRepository.sumGananciaEstimadaByFechaBetweenAndEstado(
                startOfDay, endOfDay, EstadoPedido.ENTREGADO).orElse(BigDecimal.ZERO);
    }

    // En PedidoServiceImpl.java, dentro del método getProductosMasVendidos
    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getProductosMasVendidos(int limit) {
        // Validar el límite para evitar valores negativos o cero si no es deseado
        if (limit <= 0) {
            throw new IllegalArgumentException("El límite debe ser un número positivo.");
        }

        // Creamos un objeto Pageable para pasar el límite al repositorio
        // PageRequest.of(0, limit) significa la primera página (índice 0) y 'limit' elementos por página
        org.springframework.data.domain.Pageable pageable = PageRequest.of(0, limit);

        // Llamamos al método del repositorio para obtener los productos más vendidos
        // de pedidos que ya están en estado "ENTREGADO". Esto asegura que solo contamos ventas reales.
        List<Object[]> rawData = pedidoItemRepository.findTopSellingProductsRawByEstado(EstadoPedido.ENTREGADO, pageable);

        // Procesamos la lista de Object[] para construir el Map<String, Long>
        Map<String, Long> productosMasVendidos = new HashMap<>();
        for (Object[] item : rawData) {
            Producto producto = (Producto) item[0];
            Long cantidadVendida = (Long) item[1];
            productosMasVendidos.put(producto.getNombre(), cantidadVendida);
        }

        // Aunque la consulta del repositorio ya ordena y limita,
        // este bucle asegura que el mapa se construye con los datos correctos
        // y el orden se mantiene implícitamente por cómo viene rawData.
        return productosMasVendidos;
    }


    // --- Métodos auxiliares ---

    private PedidoResponse saveAndNotify(Pedido p, String estado) {
        // Se podría validar 'p' y 'estado' aquí si se llaman desde otros lugares
        if (Objects.isNull(p) || Objects.isNull(estado) || estado.isBlank()) {
            logger.error("Parámetros nulos o vacíos al guardar y notificar pedido. Pedido: {}, Estado: {}", p, estado);
            throw new IllegalArgumentException("Los parámetros del pedido o estado no pueden ser nulos/vacíos.");
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