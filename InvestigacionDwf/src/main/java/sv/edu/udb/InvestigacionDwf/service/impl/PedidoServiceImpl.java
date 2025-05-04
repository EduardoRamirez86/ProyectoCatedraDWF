// src/main/java/sv/edu/udb/InvestigacionDwf/service/impl/PedidoServiceImpl.java
package sv.edu.udb.InvestigacionDwf.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.InvestigacionDwf.dto.request.PedidoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.PedidoResponse;
import sv.edu.udb.InvestigacionDwf.exception.ResourceNotFoundException;
import sv.edu.udb.InvestigacionDwf.model.entity.CarritoItem;
import sv.edu.udb.InvestigacionDwf.model.entity.Pedido;
import sv.edu.udb.InvestigacionDwf.repository.CarritoItemRepository;
import sv.edu.udb.InvestigacionDwf.repository.CarritoRepository;
import sv.edu.udb.InvestigacionDwf.repository.PedidoRepository;
import sv.edu.udb.InvestigacionDwf.service.PedidoService;
import sv.edu.udb.InvestigacionDwf.service.mapper.PedidoMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final PedidoMapper pedidoMapper;

    @Override
    @Transactional
    public PedidoResponse checkout(PedidoRequest req) {
        // Obtener carrito
        var carrito = carritoRepository.findById(req.getIdCarrito())
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado ID: " + req.getIdCarrito()));
        // Obtener items
        List<CarritoItem> items = carritoItemRepository.findByCarrito_IdCarrito(carrito.getIdCarrito());
        // Calcular total
        BigDecimal total = items.stream()
                .map(i -> i.getProducto().getPrecio().multiply(new BigDecimal(i.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // Crear Pedido
        Pedido pedido = pedidoMapper.toEntity(req);
        pedido.setCarrito(carrito);
        pedido.setFechaInicio(LocalDateTime.now());
        pedido.setTotal(total);
        // Guardar
        Pedido saved = pedidoRepository.save(pedido);
        return pedidoMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponse> findAllByUser(Long idUser) {
        // Se asume método custom en repo o filtrado en memoria
        List<Pedido> pedidos = pedidoRepository.findAll().stream()
                .filter(p -> p.getCarrito().getUser().getIdUser().equals(idUser))
                .collect(Collectors.toList());
        return pedidos.stream()
                .map(pedidoMapper::toResponse)
                .collect(Collectors.toList());
    }
}


