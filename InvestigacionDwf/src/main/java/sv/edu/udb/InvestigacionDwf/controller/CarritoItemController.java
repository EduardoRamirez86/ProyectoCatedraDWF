// src/main/java/sv/edu/udb/InvestigacionDwf/controller/CarritoItemController.java
package sv.edu.udb.InvestigacionDwf.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.InvestigacionDwf.dto.request.CarritoItemRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.CarritoItemResponse;
import sv.edu.udb.InvestigacionDwf.service.CarritoItemService;

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
}

