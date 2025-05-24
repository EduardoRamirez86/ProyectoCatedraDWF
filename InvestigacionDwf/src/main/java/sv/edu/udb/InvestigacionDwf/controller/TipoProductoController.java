package sv.edu.udb.InvestigacionDwf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Importa HttpStatus
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException; // Importa ResponseStatusException
import sv.edu.udb.InvestigacionDwf.dto.request.TipoProductoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.TipoProductoResponse;
import sv.edu.udb.InvestigacionDwf.service.TipoProductoService;

import java.util.List;

@RestController
@RequestMapping(path = "/auth/tipoproducto")
@CrossOrigin(origins = "http://localhost:3000")
public class TipoProductoController {

    private final TipoProductoService service;

    @Autowired
    public TipoProductoController(TipoProductoService service) {
        this.service = service;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK) // Indica que se devuelve un 200 OK
    public List<TipoProductoResponse> getAllTipoProductos() {
        return service.listarTipoProductos();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK) // Indica que se devuelve un 200 OK
    public TipoProductoResponse getTipoProductoById(@PathVariable Long id) {
        try {
            return service.obtenerTipoProductoPorId(id);
        } catch (RuntimeException e) {
            // Lanza una excepción de estado de respuesta HTTP 404 NOT FOUND si no se encuentra
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de producto no encontrado", e);
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Indica que se devuelve un 201 Created
    public TipoProductoResponse createTipoProducto(@RequestBody TipoProductoRequest req) {
        System.out.println("Datos recibidos: " + req);
        return service.crearTipoProducto(req);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK) // Indica que se devuelve un 200 OK
    public TipoProductoResponse updateTipoProducto(@PathVariable Long id,
                                                   @RequestBody TipoProductoRequest req) {
        try {
            return service.actualizarTipoProducto(id, req);
        } catch (RuntimeException e) {
            // Lanza una excepción de estado de respuesta HTTP 404 NOT FOUND si no se encuentra para actualizar
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de producto no encontrado para actualizar", e);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Indica que no hay contenido para devolver (204 No Content)
    public void deleteTipoProducto(@PathVariable Long id) {
        try {
            service.eliminarTipoProducto(id);
        } catch (RuntimeException e) {
            // Lanza una excepción de estado de respuesta HTTP 404 NOT FOUND si no se encuentra para eliminar
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de producto no encontrado para eliminar", e);
        }
    }
}

