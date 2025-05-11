// src/main/java/sv/edu/udb/InvestigacionDwf/service/impl/ProductoServiceImpl.java
package sv.edu.udb.InvestigacionDwf.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.InvestigacionDwf.dto.request.ProductoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.ProductoResponse;
import sv.edu.udb.InvestigacionDwf.exception.ResourceNotFoundException;
import sv.edu.udb.InvestigacionDwf.model.entity.CarritoItem;
import sv.edu.udb.InvestigacionDwf.model.entity.Pedido;
import sv.edu.udb.InvestigacionDwf.model.entity.Producto;
import sv.edu.udb.InvestigacionDwf.model.entity.TipoProducto;
import sv.edu.udb.InvestigacionDwf.repository.PedidoRepository;
import sv.edu.udb.InvestigacionDwf.repository.ProductoRepository;
import sv.edu.udb.InvestigacionDwf.service.ProductoService;
import sv.edu.udb.InvestigacionDwf.service.mapper.ProductoMapper;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;
    private final PedidoRepository pedidoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> findAll() {
        return productoMapper.toResponseList(productoRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse findById(Long id) {
        Producto producto = productoRepository.findByIdWithTipoProducto(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado ID: " + id));
        return productoMapper.toResponse(producto);
    }

    @Override
    @Transactional
    public ProductoResponse save(ProductoRequest request) {
        Producto entity = productoMapper.toEntity(request);
        return productoMapper.toResponse(productoRepository.save(entity));
    }

    @Override
    @Transactional
    public ProductoResponse update(Long id, ProductoRequest request) {
        Producto entity = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado ID: " + id));
        productoMapper.updateEntityFromRequest(request, entity);
        return productoMapper.toResponse(productoRepository.save(entity));
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

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> findRecommendedByUser(Long idUser) {
        // 1) Obtener todos los pedidos del usuario
        List<Pedido> pedidos = pedidoRepository.findByCarrito_User_IdUser(idUser);

        // 2) Extraer IDs de tipoProducto con referencia de método
        Set<Long> tipoIds = pedidos.stream()
                .flatMap(p -> p.getCarrito().getItems().stream())
                .map(CarritoItem::getProducto)
                .map(Producto::getTipoProducto)
                .map(TipoProducto::getIdTipoProducto)   // <-- aquí usamos method reference
                .collect(Collectors.toSet());

        if (tipoIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 3) Buscar productos de esas categorías
        List<Producto> recomendados =
                productoRepository.findByTipoProducto_IdTipoProductoIn(new ArrayList<>(tipoIds));

        // 4) Mapear a DTO y devolver
        return productoMapper.toResponseList(recomendados);
    }
}


