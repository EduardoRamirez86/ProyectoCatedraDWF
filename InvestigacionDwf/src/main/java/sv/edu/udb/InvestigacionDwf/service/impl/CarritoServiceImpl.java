/*
 Service implementations for Carrito, CarritoItem, and Pedido
*/

// src/main/java/sv/edu/udb/InvestigacionDwf/service/impl/CarritoServiceImpl.java
package sv.edu.udb.InvestigacionDwf.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.InvestigacionDwf.dto.response.CarritoItemResponse;
import sv.edu.udb.InvestigacionDwf.dto.response.CarritoResponse;
import sv.edu.udb.InvestigacionDwf.model.Carrito;
import sv.edu.udb.InvestigacionDwf.repository.CarritoItemRepository;
import sv.edu.udb.InvestigacionDwf.repository.CarritoRepository;
import sv.edu.udb.InvestigacionDwf.repository.UserRepository;
import sv.edu.udb.InvestigacionDwf.service.CarritoService;
import sv.edu.udb.InvestigacionDwf.service.mapper.CarritoItemMapper;
import sv.edu.udb.InvestigacionDwf.service.mapper.CarritoMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarritoServiceImpl implements CarritoService {

    private final CarritoRepository carritoRepository;
    private final UserRepository userRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final CarritoMapper carritoMapper;
    private final CarritoItemMapper itemMapper;

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public CarritoResponse getOrCreateByUser(Long idUser) {
        Carrito carrito = carritoRepository
                .findByUser_IdUserAndPedidosIsEmpty(idUser)
                .orElseGet(() -> {
                    Carrito nuevo = new Carrito();
                    nuevo.setUser(userRepository.getReferenceById(idUser));
                    return carritoRepository.save(nuevo);
                });
        return carritoMapper.toResponse(carrito);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public List<CarritoItemResponse> getItems(Long idCarrito) {
        return carritoItemRepository
                .findByCarrito_IdCarrito(idCarrito)
                .stream()
                .map(itemMapper::toResponse)
                .collect(Collectors.toList());
    }
}



