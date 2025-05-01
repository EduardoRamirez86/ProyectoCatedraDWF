package sv.edu.udb.InvestigacionDwf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.InvestigacionDwf.dto.response.ProductoResponse;
import sv.edu.udb.InvestigacionDwf.dto.request.ProductoRequest;
import sv.edu.udb.InvestigacionDwf.service.ProductoService;

import java.util.List;

// src/main/java/sv/edu/udb/InvestigacionDwf/controller/ProductoController.java
@RestController
@RequestMapping("auth/producto")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductoController {

    private final ProductoService service;

    @Autowired
    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductoResponse> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> getById(@PathVariable Long id) {
        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ProductoResponse create(@RequestBody ProductoRequest req) {
        return service.save(req);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> update(@PathVariable Long id,
                                                   @RequestBody ProductoRequest req) {
        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}

