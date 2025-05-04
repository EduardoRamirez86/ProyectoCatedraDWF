package sv.edu.udb.InvestigacionDwf.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<CarritoResponse> getOrCreate(@PathVariable Long idUser, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(null); // Requiere autenticación
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        if (!userDetails.getUserId().equals(idUser)) {
            return ResponseEntity.status(403).body(null); // Acceso denegado
        }
        CarritoResponse response = carritoService.getOrCreateByUser(idUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{idCarrito}/items")
    public ResponseEntity<List<CarritoItemResponse>> getItems(@PathVariable Long idCarrito, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(null); // Requiere autenticación
        }
        List<CarritoItemResponse> items = carritoService.getItems(idCarrito);
        return ResponseEntity.ok(items);
    }
}

