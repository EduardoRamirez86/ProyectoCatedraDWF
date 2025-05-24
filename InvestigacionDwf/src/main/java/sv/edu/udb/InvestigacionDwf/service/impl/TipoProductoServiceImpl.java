package sv.edu.udb.InvestigacionDwf.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importa Transactional
import sv.edu.udb.InvestigacionDwf.dto.request.TipoProductoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.TipoProductoResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.TipoProducto;
import sv.edu.udb.InvestigacionDwf.repository.TipoProductoRepository;
import sv.edu.udb.InvestigacionDwf.service.TipoProductoService;
import sv.edu.udb.InvestigacionDwf.service.mapper.TipoProductoMapper;
import sv.edu.udb.InvestigacionDwf.exception.ResourceNotFoundException; // Importa ResourceNotFoundException

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Genera un constructor con todos los campos 'final', inyectando dependencias.
public class TipoProductoServiceImpl implements TipoProductoService {

    private final TipoProductoRepository repository;
    private final TipoProductoMapper mapper; // Usado para convertir DTOs a entidades y viceversa.

    @Override
    @Transactional // Marca el método como transaccional para operaciones de escritura.
    public TipoProductoResponse crearTipoProducto(TipoProductoRequest request) {
        // Valida que el objeto request no sea nulo y que los campos esenciales no estén vacíos.
        if (Objects.isNull(request) || Objects.isNull(request.getTipo()) || request.getTipo().isBlank() ||
                Objects.isNull(request.getDescripcion()) || request.getDescripcion().isBlank()) {
            throw new IllegalArgumentException("Datos del tipo de producto inválidos: 'tipo' y 'descripción' son obligatorios.");
        }

        TipoProducto tipoProducto = mapper.toEntity(request); // Convierte el DTO de solicitud a entidad.
        return mapper.toResponse(repository.save(tipoProducto)); // Guarda la entidad y la convierte a DTO de respuesta.
    }

    @Override
    @Transactional(readOnly = true) // Marca el método como transaccional de solo lectura para optimización.
    public List<TipoProductoResponse> listarTipoProductos() {
        // Obtiene todos los tipos de producto y los mapea a DTOs de respuesta.
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true) // Marca el método como transaccional de solo lectura.
    public TipoProductoResponse obtenerTipoProductoPorId(Long id) {
        // Valida que el ID proporcionado no sea nulo.
        if (Objects.isNull(id)) {
            throw new IllegalArgumentException("El ID del tipo de producto no puede ser nulo.");
        }

        // Busca el tipo de producto por ID; si no lo encuentra, lanza una ResourceNotFoundException.
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de producto no encontrado con ID: " + id));
    }

    @Override
    @Transactional // Marca el método como transaccional para operaciones de escritura.
    public TipoProductoResponse actualizarTipoProducto(Long id, TipoProductoRequest request) {
        // Valida que el ID y el objeto request no sean nulos, y que los campos esenciales no estén vacíos.
        if (Objects.isNull(id)) {
            throw new IllegalArgumentException("El ID del tipo de producto no puede ser nulo para la actualización.");
        }
        if (Objects.isNull(request) || Objects.isNull(request.getTipo()) || request.getTipo().isBlank() ||
                Objects.isNull(request.getDescripcion()) || request.getDescripcion().isBlank()) {
            throw new IllegalArgumentException("Datos de actualización del tipo de producto inválidos: 'tipo' y 'descripción' son obligatorios.");
        }

        // Busca el tipo de producto existente por ID; si no lo encuentra, lanza una ResourceNotFoundException.
        TipoProducto existente = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de producto no encontrado con ID: " + id));

        // Actualiza los campos de la entidad existente con los datos del request.
        // Aquí podrías usar un método del mapper como `mapper.updateEntityFromRequest(request, existente);`
        // si tu TipoProductoMapper tuviera un método similar al de ProductoMapper.
        existente.setTipo(request.getTipo());
        existente.setDescripcion(request.getDescripcion());

        return mapper.toResponse(repository.save(existente)); // Guarda los cambios y convierte a DTO de respuesta.
    }

    @Override
    @Transactional // Marca el método como transaccional para operaciones de escritura.
    public void eliminarTipoProducto(Long id) {
        // Valida que el ID proporcionado no sea nulo.
        if (Objects.isNull(id)) {
            throw new IllegalArgumentException("El ID del tipo de producto no puede ser nulo para la eliminación.");
        }

        // Verifica si el tipo de producto existe antes de intentar eliminarlo.
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Tipo de producto no encontrado con ID: " + id);
        }

        repository.deleteById(id); // Elimina el tipo de producto por ID.
    }
}


