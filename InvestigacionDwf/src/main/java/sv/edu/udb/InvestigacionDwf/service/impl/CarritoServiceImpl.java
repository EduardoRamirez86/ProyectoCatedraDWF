package sv.edu.udb.InvestigacionDwf.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.InvestigacionDwf.dto.response.CarritoItemResponse;
import sv.edu.udb.InvestigacionDwf.dto.response.CarritoResponse;
import sv.edu.udb.InvestigacionDwf.exception.ResourceNotFoundException; // Importar ResourceNotFoundException
import sv.edu.udb.InvestigacionDwf.model.entity.Carrito;
import sv.edu.udb.InvestigacionDwf.model.entity.User; // Importar User
import sv.edu.udb.InvestigacionDwf.repository.CarritoItemRepository;
import sv.edu.udb.InvestigacionDwf.repository.CarritoRepository;
import sv.edu.udb.InvestigacionDwf.repository.UserRepository;
import sv.edu.udb.InvestigacionDwf.security.jwt.CustomUserDetails;
import sv.edu.udb.InvestigacionDwf.service.CarritoService;
import sv.edu.udb.InvestigacionDwf.service.mapper.CarritoItemMapper;
import sv.edu.udb.InvestigacionDwf.service.mapper.CarritoMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects; // Importar Objects
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Genera un constructor con los campos 'final' para la inyección de dependencias
public class CarritoServiceImpl implements CarritoService {

    private final CarritoRepository carritoRepository;
    private final UserRepository userRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final CarritoMapper carritoMapper;
    private final CarritoItemMapper itemMapper;

    /**
     * Recupera un carrito de compras existente para un usuario o crea uno nuevo si no existe.
     * Incluye verificaciones de seguridad para asegurar que el usuario autenticado solo pueda acceder a su propio carrito.
     *
     * @param idUser El ID del usuario cuyo carrito se va a recuperar o crear.
     * @return Un DTO CarritoResponse que representa el carrito de compras del usuario.
     * @throws IllegalArgumentException Si el ID de usuario es nulo.
     * @throws SecurityException Si la autenticación falla o el usuario intenta acceder al carrito de otro usuario.
     * @throws ResourceNotFoundException Si el usuario no se encuentra al crear un nuevo carrito.
     */
    @Override
    @Transactional // Este método realiza operaciones de lectura y escritura (guardar si es un carrito nuevo)
    public CarritoResponse getOrCreateByUser(Long idUser) {
        // Validar el ID de usuario de entrada
        if (Objects.isNull(idUser)) {
            throw new IllegalArgumentException("El ID de usuario no puede ser nulo para obtener o crear un carrito.");
        }

        // --- Verificación del Contexto de Seguridad ---
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication) || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new SecurityException("Acceso denegado: Se requiere autenticación válida.");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        // Asegurar que el usuario autenticado coincide con el ID de usuario solicitado
        if (!userDetails.getUserId().equals(idUser)) {
            throw new SecurityException("Acceso denegado: No puedes acceder al carrito de otro usuario.");
        }
        // --- Fin de la Verificación de Seguridad ---

        // Buscar carrito existente o crear uno nuevo
        Carrito carrito = carritoRepository.findByUserIdUser(idUser)
                .orElseGet(() -> {
                    // Encontrar la entidad de usuario para vincularla al nuevo carrito
                    User user = userRepository.findById(idUser)
                            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + idUser + " para crear carrito."));

                    Carrito nuevo = Carrito.builder()
                            .user(user) // Vincular la entidad User real
                            .fechaCreacion(LocalDateTime.now())
                            .build();
                    return carritoRepository.save(nuevo); // Guardar el carrito recién creado
                });
        return carritoMapper.toResponse(carrito);
    }

    /**
     * Recupera todos los ítems dentro de un carrito de compras específico.
     * Incluye verificaciones de seguridad para asegurar que el usuario autenticado solo pueda acceder a los ítems de su propio carrito.
     *
     * @param idCarrito El ID del carrito de compras cuyos ítems se van a recuperar.
     * @return Una lista de DTOs CarritoItemResponse que representan los ítems en el carrito.
     * @throws IllegalArgumentException Si el ID del carrito es nulo.
     * @throws SecurityException Si la autenticación falla o el usuario intenta acceder a los ítems del carrito de otro usuario.
     * @throws ResourceNotFoundException Si el carrito no se encuentra.
     */
    @Override
    @Transactional(readOnly = true) // Este método solo lee datos
    public List<CarritoItemResponse> getItems(Long idCarrito) {
        // Validar el ID del carrito de entrada
        if (Objects.isNull(idCarrito)) {
            throw new IllegalArgumentException("El ID del carrito no puede ser nulo para obtener los ítems.");
        }

        // --- Verificación del Contexto de Seguridad ---
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication) || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new SecurityException("Acceso denegado: Se requiere autenticación válida.");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        // --- Fin de la Verificación de Seguridad ---

        // Encontrar el carrito y validar la propiedad
        Carrito carrito = carritoRepository.findById(idCarrito)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado con ID: " + idCarrito));

        // Asegurar que el carrito pertenece al usuario autenticado
        if (Objects.isNull(carrito.getUser()) || !carrito.getUser().getIdUser().equals(userDetails.getUserId())) {
            throw new SecurityException("Acceso denegado: El carrito no pertenece al usuario autenticado.");
        }

        // Recuperar y mapear los ítems del carrito
        return carritoItemRepository
                .findByCarrito_IdCarrito(idCarrito)
                .stream()
                .map(itemMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Borra todos los ítems de un carrito de compras de un usuario después de que se ha realizado un pedido.
     * Incluye verificaciones de seguridad para asegurar que el usuario autenticado solo pueda borrar su propio carrito.
     *
     * @param idUser El ID del usuario cuyo carrito se va a vaciar.
     * @throws IllegalArgumentException Si el ID de usuario es nulo.
     * @throws SecurityException Si la autenticación falla o el usuario intenta modificar el carrito de otro usuario.
     * @throws ResourceNotFoundException Si el carrito no se encuentra.
     */
    @Transactional // Este método modifica datos
    public void clearCarritoAfterPedido(Long idUser) {
        // Validar el ID de usuario de entrada
        if (Objects.isNull(idUser)) {
            throw new IllegalArgumentException("El ID de usuario no puede ser nulo para limpiar el carrito.");
        }

        // --- Verificación del Contexto de Seguridad ---
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication) || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new SecurityException("Acceso denegado: Se requiere autenticación válida.");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        // Asegurar que el usuario autenticado coincide con el ID de usuario solicitado
        if (!userDetails.getUserId().equals(idUser)) {
            throw new SecurityException("Acceso denegado: No puedes modificar el carrito de otro usuario.");
        }
        // --- Fin de la Verificación de Seguridad ---

        // Encontrar el carrito del usuario
        Carrito carrito = carritoRepository.findByUserIdUser(idUser)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado para el usuario con ID: " + idUser));

        // Borrar todos los ítems del carrito y guardar
        carrito.getItems().clear(); // Esto asume una colección gestionada; activará eliminaciones al guardar/sincronizar
        carritoRepository.save(carrito);
    }
}
