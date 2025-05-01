// src/main/java/sv/edu/udb/InvestigacionDwf/service/impl/ProductoServiceImpl.java
package sv.edu.udb.InvestigacionDwf.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.InvestigacionDwf.dto.request.ProductoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.ProductoResponse;
import sv.edu.udb.InvestigacionDwf.exception.ResourceNotFoundException;
import sv.edu.udb.InvestigacionDwf.model.Producto;
import sv.edu.udb.InvestigacionDwf.repository.ProductoRepository;
import sv.edu.udb.InvestigacionDwf.service.ProductoService;
import sv.edu.udb.InvestigacionDwf.service.mapper.ProductoMapper;

import java.util.List;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    @Autowired
    public ProductoServiceImpl(ProductoRepository productoRepository,
                               ProductoMapper productoMapper) {
        this.productoRepository = productoRepository;
        this.productoMapper = productoMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> findAll() {
        return productoMapper.toResponseList(productoRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse findById(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado ID: " + id));
        return productoMapper.toResponse(producto);
    }

    @Override
    @Transactional
    public ProductoResponse save(ProductoRequest request) {
        Producto entity = productoMapper.toEntity(request);
        entity = productoRepository.save(entity);
        return productoMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public ProductoResponse update(Long id, ProductoRequest request) {
        Producto entity = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado ID: " + id));
        productoMapper.updateEntityFromRequest(request, entity);
        entity = productoRepository.save(entity);
        return productoMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado ID: " + id);
        }
        productoRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return productoRepository.existsById(id);
    }
}


