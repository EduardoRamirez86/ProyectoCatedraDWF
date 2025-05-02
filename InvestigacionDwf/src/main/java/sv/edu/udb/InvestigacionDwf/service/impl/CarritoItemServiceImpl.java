package sv.edu.udb.InvestigacionDwf.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sv.edu.udb.InvestigacionDwf.dto.request.CarritoItemRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.CarritoItemResponse;
import sv.edu.udb.InvestigacionDwf.model.CarritoItem;
import sv.edu.udb.InvestigacionDwf.repository.CarritoItemRepository;
import sv.edu.udb.InvestigacionDwf.repository.CarritoRepository;
import sv.edu.udb.InvestigacionDwf.repository.ProductoRepository;
import sv.edu.udb.InvestigacionDwf.service.CarritoItemService;
import sv.edu.udb.InvestigacionDwf.service.mapper.CarritoItemMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarritoItemServiceImpl implements CarritoItemService {

    private final CarritoItemRepository carritoItemRepository;
    private final ProductoRepository productoRepository;
    private final CarritoRepository carritoRepository;
    private final CarritoItemMapper carritoItemMapper;

    @Override
    public CarritoItemResponse addItem(CarritoItemRequest request) {
        // Convertir el request a entidad CarritoItem usando el mapper
        CarritoItem newItem = carritoItemMapper.toEntity(request);
        // Guardar la entidad en el repositorio
        CarritoItem savedItem = carritoItemRepository.save(newItem);
        // Devolver la respuesta mapeada
        return carritoItemMapper.toResponse(savedItem);
    }

    @Override
    public void removeItem(Long idCarritoItem) {
        // Buscar el item en el repositorio
        CarritoItem itemToRemove = carritoItemRepository.findById(idCarritoItem)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));
        // Eliminar el item
        carritoItemRepository.delete(itemToRemove);
    }

    @Override
    public List<CarritoItemResponse> getItemsByCarritoId(Long idCarrito) {
        // Obtener todos los items y filtrar por carrito
        return carritoItemRepository.findAll().stream()
                .filter(item -> item.getCarrito().getIdCarrito().equals(idCarrito))
                .map(carritoItemMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CarritoItemResponse updateItem(Long id, CarritoItemRequest request) {
        // Buscar el item existente por su ID
        CarritoItem existingItem = carritoItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        // Actualizar los valores del item
        existingItem.setCantidad(request.getCantidad());
        existingItem.setProducto(productoRepository.getReferenceById(request.getIdProducto()));
        existingItem.setCarrito(carritoRepository.getReferenceById(request.getIdCarrito()));

        // Guardar el item actualizado
        CarritoItem updatedItem = carritoItemRepository.save(existingItem);

        // Devolver la respuesta mapeada
        return carritoItemMapper.toResponse(updatedItem);
    }

    @Override
    public List<CarritoItemResponse> getAllItems() {
        // Obtener todos los items y devolverlos como respuesta
        return carritoItemRepository.findAll().stream()
                .map(carritoItemMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CarritoItemResponse getItemById(Long id) {
        // Buscar el item por su ID
        CarritoItem item = carritoItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));
        // Devolver la respuesta mapeada
        return carritoItemMapper.toResponse(item);
    }
}

