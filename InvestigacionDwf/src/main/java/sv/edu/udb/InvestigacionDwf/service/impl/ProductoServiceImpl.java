package sv.edu.udb.InvestigacionDwf.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.InvestigacionDwf.dto.request.ProductoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.ProductoResponse;
import sv.edu.udb.InvestigacionDwf.exception.ResourceNotFoundException;
import sv.edu.udb.InvestigacionDwf.model.entity.Producto;
import sv.edu.udb.InvestigacionDwf.repository.ProductoRepository;
import sv.edu.udb.InvestigacionDwf.service.ProductoService;
import sv.edu.udb.InvestigacionDwf.service.assembler.ProductoAssembler;
import sv.edu.udb.InvestigacionDwf.service.mapper.ProductoMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects; // Importa Objects para las validaciones de nulidad

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository repo;
    private final ProductoAssembler assembler;
    private final ProductoMapper mapper; // Usado para convertir DTOs a entidades
    private final PagedResourcesAssembler<Producto> pagedAssembler;

    @Override
    @Transactional
    public ProductoResponse create(ProductoRequest req) {
        // Valida que el request no sea nulo antes de procesar
        if (Objects.isNull(req)) {
            throw new IllegalArgumentException("La solicitud de creación de producto no puede ser nula.");
        }
        Producto prod = mapper.toEntity(req); // Convierte el DTO a entidad usando el mapper
        prod.setFechaCreacion(LocalDateTime.now()); // Establece la fecha de creación
        Producto saved = repo.save(prod); // Guarda el nuevo producto en la base de datos
        return assembler.toModel(saved); // Convierte la entidad guardada a un DTO de respuesta
    }

    @Override
    @Transactional
    public ProductoResponse update(Long id, ProductoRequest req) {
        // Valida que el ID y el request no sean nulos
        if (Objects.isNull(id)) {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo para la actualización.");
        }
        if (Objects.isNull(req)) {
            throw new IllegalArgumentException("La solicitud de actualización de producto no puede ser nula.");
        }

        // Busca el producto por ID o lanza una excepción si no se encuentra
        Producto prod = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado ID: " + id));

        mapper.updateEntityFromRequest(req, prod); // Actualiza la entidad con los datos del request usando el mapper
        prod.setFechaActualizacion(LocalDateTime.now()); // Establece la fecha de última actualización
        Producto updated = repo.save(prod); // Guarda los cambios en el producto
        return assembler.toModel(updated); // Convierte la entidad actualizada a un DTO de respuesta
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // Valida que el ID no sea nulo
        if (Objects.isNull(id)) {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo para la eliminación.");
        }
        // Verifica si el producto existe antes de intentar eliminarlo
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado ID: " + id);
        }
        repo.deleteById(id); // Elimina el producto por ID
    }

    @Override
    @Transactional(readOnly = true)
    public PagedModel<ProductoResponse> findAll(Pageable pageable) {
        // Valida que el objeto Pageable no sea nulo
        if (Objects.isNull(pageable)) {
            throw new IllegalArgumentException("La paginación no puede ser nula.");
        }
        Page<Producto> page = repo.findAll(pageable); // Obtiene una página de productos
        return pagedAssembler.toModel(page, assembler); // Convierte la página de entidades a un modelo paginado de DTOs HATEOAS
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse getById(Long id) {
        // Valida que el ID no sea nulo
        if (Objects.isNull(id)) {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo.");
        }
        // Busca el producto por ID (posiblemente con su tipo de producto asociado) o lanza una excepción
        Producto prod = repo.findByIdWithTipoProducto(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado ID: " + id));
        return assembler.toModel(prod); // Convierte la entidad encontrada a un DTO de respuesta
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> findRecommendedByUser(Long idUser) {
        // Valida que el ID de usuario no sea nulo
        if (Objects.isNull(idUser)) {
            // Aunque este método no usa idUser directamente para la búsqueda, es una buena práctica validarlo si se pasa.
            // Si findTop5ByOrderByFechaCreacionDesc() es la lógica actual, idUser podría no ser estrictamente necesario,
            // pero la validación refuerza el contrato.
            throw new IllegalArgumentException("El ID de usuario no puede ser nulo para la recomendación.");
        }
        // Obtiene los 5 productos más recientes (asumiendo que es la lógica de "recomendados")
        return repo.findTop5ByOrderByFechaCreacionDesc()
                .stream()
                .map(assembler::toModel) // Mapea cada entidad a su DTO de respuesta
                .toList(); // Recopila los DTOs en una lista
    }
}

