// src/main/java/sv/edu/udb/InvestigacionDwf/controller/CarritoController.java
package sv.edu.udb.InvestigacionDwf.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.InvestigacionDwf.dto.response.CarritoItemResponse;
import sv.edu.udb.InvestigacionDwf.dto.response.CarritoResponse;
import sv.edu.udb.InvestigacionDwf.service.CarritoService;

import java.util.List;

@RestController
@RequestMapping(path = "auth/carrito")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;

    @GetMapping("/{idUser}")
    public ResponseEntity<CarritoResponse> getOrCreate(@PathVariable Long idUser) {
        CarritoResponse response = carritoService.getOrCreateByUser(idUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{idCarrito}/items")
    public ResponseEntity<List<CarritoItemResponse>> getItems(@PathVariable Long idCarrito) {
        List<CarritoItemResponse> items = carritoService.getItems(idCarrito);
        return ResponseEntity.ok(items);
    }
}

