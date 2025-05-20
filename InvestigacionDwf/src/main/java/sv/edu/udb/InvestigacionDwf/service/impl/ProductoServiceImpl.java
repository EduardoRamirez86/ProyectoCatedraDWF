// src/main/java/sv/edu/udb/InvestigacionDwf/service/impl/ProductoServiceImpl.java
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
import sv.edu.udb.InvestigacionDwf.service.assembler.ProductoAssembler;
import sv.edu.udb.InvestigacionDwf.service.mapper.ProductoMapper;
import sv.edu.udb.InvestigacionDwf.service.ProductoService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository repo;
    private final ProductoAssembler assembler;
    private final ProductoMapper mapper;
    private final PagedResourcesAssembler<Producto> pagedAssembler;

    @Override
    @Transactional
    public ProductoResponse create(ProductoRequest req) {
        Producto prod = mapper.toEntity(req);
        prod.setFechaCreacion(java.time.LocalDateTime.now());
        Producto saved = repo.save(prod);
        return assembler.toModel(saved);
    }

    @Override
    @Transactional
    public ProductoResponse update(Long id, ProductoRequest req) {
        Producto prod = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado ID: " + id));
        mapper.updateEntityFromRequest(req, prod);
        prod.setFechaActualizacion(java.time.LocalDateTime.now());
        Producto updated = repo.save(prod);
        return assembler.toModel(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado ID: " + id);
        }
        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedModel<ProductoResponse> findAll(Pageable pageable) {
        Page<Producto> page = repo.findAll(pageable);
        return pagedAssembler.toModel(page, assembler);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse getById(Long id) {
        Producto prod = repo.findByIdWithTipoProducto(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado ID: " + id));
        return assembler.toModel(prod);
    }
    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> findRecommendedByUser(Long idUser) {
        // Esta es una lógica de ejemplo. Puedes personalizarla según tu necesidad real.
        // Por ejemplo, podrías filtrar por tipo de producto más comprado por el usuario, etc.

        // Aquí simplemente devolvemos los últimos 5 productos creados como "recomendados"
        return repo.findTop5ByOrderByFechaCreacionDesc()
                .stream()
                .map(assembler::toModel)
                .toList();
    }

}

