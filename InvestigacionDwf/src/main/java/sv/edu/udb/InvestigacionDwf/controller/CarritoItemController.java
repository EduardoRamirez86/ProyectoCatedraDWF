package sv.edu.udb.InvestigacionDwf.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.InvestigacionDwf.dto.request.CarritoItemRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.CarritoItemResponse;
import sv.edu.udb.InvestigacionDwf.service.CarritoItemService;

import java.util.List;

@RestController
@RequestMapping(path = "auth/carrito-item")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class CarritoItemController {

    private final CarritoItemService itemService;

    @PostMapping
    public ResponseEntity<CarritoItemResponse> addItem(@RequestBody CarritoItemRequest req) {
        CarritoItemResponse created = itemService.addItem(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeItem(@PathVariable Long id) {
        itemService.removeItem(id);
        return ResponseEntity.noContent().build();
    }

    // Obtener todos los items del carrito
    @GetMapping("/carrito/{idCarrito}")
    public ResponseEntity<List<CarritoItemResponse>> getItemsByCarrito(@PathVariable Long idCarrito) {
        List<CarritoItemResponse> items = itemService.getItemsByCarritoId(idCarrito);
        return ResponseEntity.ok(items);
    }

    // Obtener todos los items
    @GetMapping
    public ResponseEntity<List<CarritoItemResponse>> getAllItems() {
        List<CarritoItemResponse> allItems = itemService.getAllItems();
        return ResponseEntity.ok(allItems);
    }

    // Obtener un item por su ID
    @GetMapping("/{id}")
    public ResponseEntity<CarritoItemResponse> getItemById(@PathVariable Long id) {
        CarritoItemResponse item = itemService.getItemById(id);
        return ResponseEntity.ok(item);
    }

    // Actualizar un item
    @PutMapping("/{id}")
    public ResponseEntity<CarritoItemResponse> updateItem(@PathVariable Long id, @RequestBody CarritoItemRequest request) {
        CarritoItemResponse updated = itemService.updateItem(id, request);
        return ResponseEntity.ok(updated);
    }
}

