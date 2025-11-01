package sv.edu.udb.InvestigacionDwf.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.InvestigacionDwf.dto.request.CarritoItemRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.CarritoItemResponse;
import sv.edu.udb.InvestigacionDwf.exception.ResourceNotFoundException;
import sv.edu.udb.InvestigacionDwf.model.entity.Carrito;        // Importar Carrito
import sv.edu.udb.InvestigacionDwf.model.entity.CarritoItem;
import sv.edu.udb.InvestigacionDwf.model.entity.Producto;      // Importar Producto
import sv.edu.udb.InvestigacionDwf.repository.CarritoItemRepository;
import sv.edu.udb.InvestigacionDwf.repository.CarritoRepository;
import sv.edu.udb.InvestigacionDwf.repository.ProductoRepository;
import sv.edu.udb.InvestigacionDwf.service.CarritoItemService;
import sv.edu.udb.InvestigacionDwf.service.mapper.CarritoItemMapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarritoItemServiceImpl implements CarritoItemService {

    private final CarritoItemRepository carritoItemRepository;
    private final ProductoRepository productoRepository;
    private final CarritoRepository carritoRepository;
    private final CarritoItemMapper carritoItemMapper;

    @Override
    @Transactional
    public CarritoItemResponse addItem(CarritoItemRequest request) {
        if (Objects.isNull(request) || Objects.isNull(request.getIdProducto()) || Objects.isNull(request.getIdCarrito()) || request.getCantidad() <= 0) {
            throw new IllegalArgumentException("Datos del ítem del carrito inválidos.");
        }

        // --- 👇👇👇 ¡LA LÓGICA DE LA VICTORIA! 👇👇👇 ---

        // 1. OBTENER las entidades completas ANTES de hacer nada más.
        //    Esto asegura que no sean proxies perezosos y que tengamos los objetos reales.
        Producto producto = productoRepository.findById(request.getIdProducto())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + request.getIdProducto()));

        Carrito carrito = carritoRepository.findById(request.getIdCarrito())
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado con ID: " + request.getIdCarrito()));

        // 2. CREAR el nuevo ítem y ASIGNAR las entidades ya cargadas.
        CarritoItem newItem = new CarritoItem();
        newItem.setProducto(producto); // Asignamos el objeto Producto completo
        newItem.setCarrito(carrito);   // Asignamos el objeto Carrito completo
        newItem.setCantidad(request.getCantidad());

        // 3. GUARDAR la nueva entidad.
        CarritoItem savedItem = carritoItemRepository.save(newItem);

        // 4. MAPEAR la respuesta. Ahora el mapper puede acceder a savedItem.getProducto()
        //    y savedItem.getCarrito() de forma segura, porque ya los teníamos en memoria.
        return carritoItemMapper.toResponse(savedItem);
        // --- -------------------------------------------- ---
    }

    // ... (El resto de tus métodos: removeItem, getItemsByCarritoId, etc., están bien y no necesitan cambios)

    @Override
    @Transactional
    public void removeItem(Long idCarritoItem) {
        if (Objects.isNull(idCarritoItem)) {
            throw new IllegalArgumentException("El ID del ítem del carrito no puede ser nulo para eliminarlo.");
        }
        CarritoItem itemToRemove = carritoItemRepository.findById(idCarritoItem)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem del carrito no encontrado con ID: " + idCarritoItem));
        carritoItemRepository.delete(itemToRemove);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarritoItemResponse> getItemsByCarritoId(Long idCarrito) {
        if (Objects.isNull(idCarrito)) {
            throw new IllegalArgumentException("El ID del carrito no puede ser nulo para obtener sus ítems.");
        }
        return carritoItemRepository.findByCarrito_IdCarrito(idCarrito).stream()
                .map(carritoItemMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CarritoItemResponse updateItem(Long id, CarritoItemRequest request) {
        if (Objects.isNull(id)) {
            throw new IllegalArgumentException("El ID del ítem del carrito no puede ser nulo para actualizar.");
        }
        if (Objects.isNull(request)) {
            throw new IllegalArgumentException("La solicitud de actualización de ítem del carrito no puede ser nula.");
        }
        if (request.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad del producto debe ser mayor a cero.");
        }
        if (Objects.nonNull(request.getIdProducto())) {
            productoRepository.findById(request.getIdProducto())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + request.getIdProducto()));
        }
        if (Objects.nonNull(request.getIdCarrito())) {
            carritoRepository.findById(request.getIdCarrito())
                    .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado con ID: " + request.getIdCarrito()));
        }

        CarritoItem existingItem = carritoItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem del carrito no encontrado con ID: " + id));

        carritoItemMapper.updateEntityFromRequest(request, existingItem);
        CarritoItem updatedItem = carritoItemRepository.save(existingItem);
        return carritoItemMapper.toResponse(updatedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarritoItemResponse> getAllItems() {
        return carritoItemRepository.findAll().stream()
                .map(carritoItemMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CarritoItemResponse getItemById(Long id) {
        if (Objects.isNull(id)) {
            throw new IllegalArgumentException("El ID del ítem del carrito no puede ser nulo para obtenerlo.");
        }
        CarritoItem item = carritoItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem del carrito no encontrado con ID: " + id));
        return carritoItemMapper.toResponse(item);
    }
}
