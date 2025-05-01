package sv.edu.udb.InvestigacionDwf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.InvestigacionDwf.dto.request.TipoProductoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.TipoProductoResponse;
import sv.edu.udb.InvestigacionDwf.service.TipoProductoService;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping(path = "auth/tipoproducto")
@CrossOrigin(origins = "http://localhost:3000")
public class TipoProductoController {

    private final TipoProductoService service;

    @Autowired
    public TipoProductoController(TipoProductoService service) {
        this.service = service;
    }

    @GetMapping
    public List<TipoProductoResponse> getAllTipoProductos() {
        return service.listarTipoProductos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoProductoResponse> getTipoProductoById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.obtenerTipoProductoPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public TipoProductoResponse createTipoProducto(@RequestBody TipoProductoRequest req) {
        System.out.println("Datos recibidos: " + req);
        return service.crearTipoProducto(req);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoProductoResponse> updateTipoProducto(@PathVariable Long id,
                                                                   @RequestBody TipoProductoRequest req) {
        try {
            return ResponseEntity.ok(service.actualizarTipoProducto(id, req));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public ResponseEntity<?> deleteTipoProducto(@PathVariable Long id) {
        try {
            service.eliminarTipoProducto(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

