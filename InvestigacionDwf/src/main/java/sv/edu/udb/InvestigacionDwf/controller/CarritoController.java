package sv.edu.udb.InvestigacionDwf.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.InvestigacionDwf.dto.response.CarritoItemResponse;
import sv.edu.udb.InvestigacionDwf.dto.response.CarritoResponse;
import sv.edu.udb.InvestigacionDwf.security.jwt.CustomUserDetails;
import sv.edu.udb.InvestigacionDwf.service.CarritoService;

import java.util.List;

@RestController
@RequestMapping(path = "/auth/carrito")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;

    @GetMapping("/{idUser}")
    @ResponseStatus(HttpStatus.OK) // Establece el código de estado HTTP a 200 OK
    public CarritoResponse getOrCreate(@PathVariable Long idUser, Authentication authentication) {
        // Obtiene los detalles del usuario autenticado
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // Verifica si el ID de usuario en la ruta coincide con el ID del usuario autenticado
        if (!userDetails.getUserId().equals(idUser)) {
            // Lanza una excepción si el usuario no está autorizado para acceder a este carrito
            throw new AccessDeniedException("No estás autorizado para acceder al carrito de este usuario.");
        }
        // Llama al servicio para obtener o crear el carrito
        return carritoService.getOrCreateByUser(idUser);
    }

    @GetMapping("/{idCarrito}/items")
    @ResponseStatus(HttpStatus.OK) // Establece el código de estado HTTP a 200 OK
    public List<CarritoItemResponse> getItems(@PathVariable Long idCarrito) {
        // Llama al servicio para obtener los ítems del carrito
        // (Considera agregar una verificación aquí para asegurar que el usuario autenticado es dueño de este carrito,
        // o que el servicio ya lo maneja).
        return carritoService.getItems(idCarrito);
    }
}
