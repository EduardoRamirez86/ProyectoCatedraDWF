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
import sv.edu.udb.InvestigacionDwf.service.ProductoService;
import sv.edu.udb.InvestigacionDwf.model.entity.Producto;

@RestController
@RequestMapping("/auth/producto")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService service;
    private final PagedResourcesAssembler<Producto> assembler;

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
    public PagedModel<ProductoResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ProductoResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/recomendados/{idUser}")
    public CollectionModel<ProductoResponse> getRecommendedByUser(@PathVariable Long idUser) {
        var recomendaciones = service.findRecommendedByUser(idUser);
        return CollectionModel.of(recomendaciones);
    }
}  



