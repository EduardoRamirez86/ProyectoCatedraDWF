package sv.edu.udb.InvestigacionDwf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.InvestigacionDwf.dto.response.ProductoResponse;
import sv.edu.udb.InvestigacionDwf.dto.request.ProductoRequest;
import sv.edu.udb.InvestigacionDwf.service.ProductoService;

import java.util.List;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping(path = "auth/producto")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductoController {

    private final ProductoService service;

    @Autowired
    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductoResponse> getAllProductos() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> getProductoById(@PathVariable Long id) {
        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public ProductoResponse createProducto(@RequestBody ProductoRequest req) {
        System.out.println("Datos recibidos: " + req);
        return service.save(req);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> updateProducto(@PathVariable Long id,
                                                           @RequestBody ProductoRequest req) {
        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public ResponseEntity<?> deleteProducto(@PathVariable Long id) {
        if (!service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    // NUEVO: endpoint para productos recomendados por usuario
    @GetMapping("/recomendados/{idUser}")
    public ResponseEntity<List<ProductoResponse>> getRecomendadosByUser(@PathVariable Long idUser) {
        List<ProductoResponse> recomendaciones = service.findRecommendedByUser(idUser);
        if (recomendaciones.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(recomendaciones);
    }
}

