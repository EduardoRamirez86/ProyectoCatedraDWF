package sv.edu.udb.InvestigacionDwf.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.*;

import sv.edu.udb.InvestigacionDwf.dto.request.ProductoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.ProductoResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.Producto;
import sv.edu.udb.InvestigacionDwf.service.ProductoService;

@RestController
@RequestMapping("/auth/producto")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService service;

    /**
     * Crea un nuevo producto.
     */
    @PostMapping
    public ProductoResponse create(@RequestBody ProductoRequest req) {
        return service.create(req);
    }

    /**
     * Actualiza un producto existente.
     */
    @PutMapping("/{id}")
    public ProductoResponse update(
            @PathVariable Long id,
            @RequestBody ProductoRequest req
    ) {
        return service.update(id, req);
    }

    /**
     * Elimina un producto por su ID.
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    /**
     * Devuelve todos los productos paginados.
     * Ejemplo: GET /auth/producto/all?page=0&size=10
     */
    @GetMapping("/all")
    public PagedModel<ProductoResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            PagedResourcesAssembler<Producto> assembler
    ) {
        Pageable pageable = PageRequest.of(page, size);
        // Asume que service.findAll devuelve un PagedModel<ProductoResponse>
        return service.findAll(pageable);
    }

    /**
     * Obtiene un producto por su ID.
     */
    @GetMapping("/{id}")
    public ProductoResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    /**
     * Devuelve productos recomendados para un usuario.
     */
    @GetMapping("/recomendados/{idUser}")
    public CollectionModel<ProductoResponse> getRecommendedByUser(
            @PathVariable Long idUser
    ) {
        var recomendaciones = service.findRecommendedByUser(idUser);
        return CollectionModel.of(recomendaciones);
    }
}
