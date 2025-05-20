package sv.edu.udb.InvestigacionDwf.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.InvestigacionDwf.dto.request.ProductoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.ProductoResponse;
import sv.edu.udb.InvestigacionDwf.service.ProductoService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/auth/producto")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService service;
    private final PagedResourcesAssembler<sv.edu.udb.InvestigacionDwf.model.entity.Producto> assembler;

    @PostMapping
    public ProductoResponse create(@RequestBody ProductoRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public ProductoResponse update(@PathVariable Long id, @RequestBody ProductoRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/all")
    public PagedModel<ProductoResponse> getAll(Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ProductoResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    /**
     * NUEVO: productos recomendados para un usuario (sin paginar)
     */
    @GetMapping("/recomendados/{idUser}")
    public CollectionModel<ProductoResponse> getRecommendedByUser(@PathVariable Long idUser) {
        var recomendaciones = service.findRecommendedByUser(idUser);

        return CollectionModel.of(recomendaciones,
                linkTo(methodOn(ProductoController.class)
                        .getRecommendedByUser(idUser))
                        .withSelfRel(),
                linkTo(methodOn(ProductoController.class)
                        .getAll(null))
                        .withRel("productos")
        );
    }

}


