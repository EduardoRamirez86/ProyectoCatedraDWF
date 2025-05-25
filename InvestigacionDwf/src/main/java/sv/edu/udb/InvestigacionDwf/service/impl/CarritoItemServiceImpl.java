package sv.edu.udb.InvestigacionDwf.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar Transactional
import sv.edu.udb.InvestigacionDwf.dto.request.CarritoItemRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.CarritoItemResponse;
import sv.edu.udb.InvestigacionDwf.exception.ResourceNotFoundException; // Importar ResourceNotFoundException
import sv.edu.udb.InvestigacionDwf.model.entity.CarritoItem;
import sv.edu.udb.InvestigacionDwf.repository.CarritoItemRepository;
import sv.edu.udb.InvestigacionDwf.repository.CarritoRepository;
import sv.edu.udb.InvestigacionDwf.repository.ProductoRepository;
import sv.edu.udb.InvestigacionDwf.service.CarritoItemService;
import sv.edu.udb.InvestigacionDwf.service.mapper.CarritoItemMapper;

import java.util.List;
import java.util.Objects; // Importar Objects
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Genera un constructor con los campos 'final' para inyección de dependencias
public class CarritoItemServiceImpl implements CarritoItemService {

    private final CarritoItemRepository carritoItemRepository;
    private final ProductoRepository productoRepository; // Necesario para validar existencia y obtener referencias
    private final CarritoRepository carritoRepository;   // Necesario para validar existencia y obtener referencias
    private final CarritoItemMapper carritoItemMapper;

    /**
     * Agrega un nuevo ítem al carrito de compras.
     * Realiza validaciones de los datos de entrada y asegura la existencia del producto y del carrito.
     *
     * @param request El DTO de solicitud con los detalles del ítem del carrito.
     * @return Un DTO CarritoItemResponse del ítem agregado.
     * @throws IllegalArgumentException Si el request o sus IDs de carrito/producto son nulos.
     * @throws ResourceNotFoundException Si el producto o el carrito referenciado no existen.
     */
    @Override
    @Transactional // Esta operación modifica el estado de la base de datos
    public CarritoItemResponse addItem(CarritoItemRequest request) {
        // --- Validaciones de entrada ---
        if (Objects.isNull(request)) {
            throw new IllegalArgumentException("La solicitud para añadir un ítem al carrito no puede ser nula.");
        }
        if (Objects.isNull(request.getIdProducto())) {
            throw new IllegalArgumentException("El ID del producto es obligatorio para añadir un ítem al carrito.");
        }
        if (Objects.isNull(request.getIdCarrito())) {
            throw new IllegalArgumentException("El ID del carrito es obligatorio para añadir un ítem al carrito.");
        }
        if (request.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad del producto debe ser mayor a cero.");
        }

        // Verificar que el Producto y el Carrito existen antes de crear el ítem
        productoRepository.findById(request.getIdProducto())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + request.getIdProducto()));
        carritoRepository.findById(request.getIdCarrito())
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado con ID: " + request.getIdCarrito()));

        // Convertir el request a entidad CarritoItem usando el mapper
        // El mapper se encargará de obtener las referencias a Carrito y Producto
        CarritoItem newItem = carritoItemMapper.toEntity(request);
        // Guardar la entidad en el repositorio
        CarritoItem savedItem = carritoItemRepository.save(newItem);
        // Devolver la respuesta mapeada
        return carritoItemMapper.toResponse(savedItem);
    }

    /**
     * Elimina un ítem específico del carrito de compras por su ID.
     *
     * @param idCarritoItem El ID del ítem del carrito a eliminar.
     * @throws IllegalArgumentException Si el ID del ítem del carrito es nulo.
     * @throws ResourceNotFoundException Si el ítem del carrito no se encuentra.
     */
    @Override
    @Transactional // Esta operación modifica el estado de la base de datos
    public void removeItem(Long idCarritoItem) {
        // Validar ID de entrada
        if (Objects.isNull(idCarritoItem)) {
            throw new IllegalArgumentException("El ID del ítem del carrito no puede ser nulo para eliminarlo.");
        }

        // Buscar el ítem en el repositorio; si no se encuentra, lanzar ResourceNotFoundException.
        CarritoItem itemToRemove = carritoItemRepository.findById(idCarritoItem)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem del carrito no encontrado con ID: " + idCarritoItem));
        // Eliminar el ítem
        carritoItemRepository.delete(itemToRemove);
    }

    /**
     * Recupera todos los ítems para un carrito de compras dado su ID.
     *
     * @param idCarrito El ID del carrito.
     * @return Una lista de DTOs CarritoItemResponse.
     * @throws IllegalArgumentException Si el ID del carrito es nulo.
     */
    @Override
    @Transactional(readOnly = true) // Este método solo lee datos
    public List<CarritoItemResponse> getItemsByCarritoId(Long idCarrito) {
        // Validar ID de entrada
        if (Objects.isNull(idCarrito)) {
            throw new IllegalArgumentException("El ID del carrito no puede ser nulo para obtener sus ítems.");
        }

        // Se usa findByCarrito_IdCarrito para una consulta optimizada directamente al repositorio,
        // en lugar de findAll().stream().filter(), que podría ser ineficiente para muchos ítems.
        return carritoItemRepository.findByCarrito_IdCarrito(idCarrito).stream()
                .map(carritoItemMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza un ítem existente en el carrito de compras.
     * Permite actualizar la cantidad, producto o carrito asociado al ítem.
     *
     * @param id El ID del ítem del carrito a actualizar.
     * @param request El DTO de solicitud con los datos actualizados.
     * @return Un DTO CarritoItemResponse del ítem actualizado.
     * @throws IllegalArgumentException Si los IDs o el request son nulos/inválidos, o la cantidad es <= 0.
     * @throws ResourceNotFoundException Si el ítem, producto o carrito referenciado no existen.
     */
    @Override
    @Transactional // Esta operación modifica el estado de la base de datos
    public CarritoItemResponse updateItem(Long id, CarritoItemRequest request) {
        // --- Validaciones de entrada ---
        if (Objects.isNull(id)) {
            throw new IllegalArgumentException("El ID del ítem del carrito no puede ser nulo para actualizar.");
        }
        if (Objects.isNull(request)) {
            throw new IllegalArgumentException("La solicitud de actualización de ítem del carrito no puede ser nula.");
        }
        if (request.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad del producto debe ser mayor a cero.");
        }
        // Validar que los IDs de producto y carrito en el request no sean nulos si se van a actualizar
        if (Objects.nonNull(request.getIdProducto())) {
            productoRepository.findById(request.getIdProducto())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + request.getIdProducto()));
        }
        if (Objects.nonNull(request.getIdCarrito())) {
            carritoRepository.findById(request.getIdCarrito())
                    .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado con ID: " + request.getIdCarrito()));
        }

        // Buscar el ítem existente por su ID; si no se encuentra, lanzar ResourceNotFoundException.
        CarritoItem existingItem = carritoItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem del carrito no encontrado con ID: " + id));

        // Actualizar los valores del ítem usando el mapper (preferible) o manualmente
        // Aquí se usa un método de actualización en el mapper para manejar la lógica de qué campos actualizar
        carritoItemMapper.updateEntityFromRequest(request, existingItem);

        // Guardar el ítem actualizado
        CarritoItem updatedItem = carritoItemRepository.save(existingItem);

        // Devolver la respuesta mapeada
        return carritoItemMapper.toResponse(updatedItem);
    }

    /**
     * Recupera todos los ítems de todos los carritos. (Usar con precaución, puede ser costoso).
     *
     * @return Una lista de DTOs CarritoItemResponse de todos los ítems.
     */
    @Override
    @Transactional(readOnly = true) // Este método solo lee datos
    public List<CarritoItemResponse> getAllItems() {
        // Obtener todos los ítems y devolverlos como respuesta
        return carritoItemRepository.findAll().stream()
                .map(carritoItemMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Recupera un ítem específico del carrito por su ID.
     *
     * @param id El ID del ítem del carrito.
     * @return Un DTO CarritoItemResponse del ítem encontrado.
     * @throws IllegalArgumentException Si el ID del ítem es nulo.
     * @throws ResourceNotFoundException Si el ítem del carrito no se encuentra.
     */
    @Override
    @Transactional(readOnly = true) // Este método solo lee datos
    public CarritoItemResponse getItemById(Long id) {
        // Validar ID de entrada
        if (Objects.isNull(id)) {
            throw new IllegalArgumentException("El ID del ítem del carrito no puede ser nulo para obtenerlo.");
        }

        // Buscar el ítem por su ID; si no se encuentra, lanzar ResourceNotFoundException.
        CarritoItem item = carritoItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem del carrito no encontrado con ID: " + id));
        // Devolver la respuesta mapeada
        return carritoItemMapper.toResponse(item);
    }
}

