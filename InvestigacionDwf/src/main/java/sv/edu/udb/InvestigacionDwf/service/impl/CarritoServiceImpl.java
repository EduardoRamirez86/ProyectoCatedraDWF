package sv.edu.udb.InvestigacionDwf.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.InvestigacionDwf.dto.response.CarritoItemResponse;
import sv.edu.udb.InvestigacionDwf.dto.response.CarritoResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.Carrito;
import sv.edu.udb.InvestigacionDwf.repository.CarritoItemRepository;
import sv.edu.udb.InvestigacionDwf.repository.CarritoRepository;
import sv.edu.udb.InvestigacionDwf.repository.UserRepository;
import sv.edu.udb.InvestigacionDwf.security.jwt.CustomUserDetails;
import sv.edu.udb.InvestigacionDwf.service.CarritoService;
import sv.edu.udb.InvestigacionDwf.service.mapper.CarritoItemMapper;
import sv.edu.udb.InvestigacionDwf.service.mapper.CarritoMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication) || !authentication.isAuthenticated()) {
            throw new SecurityException("Acceso denegado: Se requiere autenticación");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        if (!userDetails.getUserId().equals(idUser)) {
            throw new SecurityException("Acceso denegado: No puedes acceder al carrito de otro usuario");
        }

        Carrito carrito = carritoRepository.findByUserIdUser(idUser)
                .orElseGet(() -> {
                    Carrito nuevo = Carrito.builder()
                            .user(userRepository.getReferenceById(idUser))
                            .fechaCreacion(LocalDateTime.now())
                            .build();
                    return carritoRepository.save(nuevo);
                });
        return carritoMapper.toResponse(carrito);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarritoItemResponse> getItems(Long idCarrito) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication) || !authentication.isAuthenticated()) {
            throw new SecurityException("Acceso denegado: Se requiere autenticación");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Carrito carrito = carritoRepository.findById(idCarrito)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        if (!carrito.getUser().getIdUser().equals(userDetails.getUserId())) {
            throw new SecurityException("Acceso denegado: Carrito no pertenece al usuario");
        }

        return carritoItemRepository
                .findByCarrito_IdCarrito(idCarrito)
                .stream()
                .map(itemMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void clearCarritoAfterPedido(Long idUser) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication) || !authentication.isAuthenticated()) {
            throw new SecurityException("Acceso denegado: Se requiere autenticación");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        if (!userDetails.getUserId().equals(idUser)) {
            throw new SecurityException("Acceso denegado: No puedes modificar el carrito de otro usuario");
        }

        Carrito carrito = carritoRepository.findByUserIdUser(idUser)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        carrito.getItems().clear();
        carritoRepository.save(carrito);
    }
}
