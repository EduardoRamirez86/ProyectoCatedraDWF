package sv.edu.udb.InvestigacionDwf.service.assembler;

import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import sv.edu.udb.InvestigacionDwf.controller.ProductoController;
import sv.edu.udb.InvestigacionDwf.dto.response.ProductoResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.Producto;
import sv.edu.udb.InvestigacionDwf.service.mapper.ProductoMapper;

@Component
public class ProductoAssembler implements RepresentationModelAssembler<Producto, ProductoResponse> {

    private final ProductoMapper mapper;

    public ProductoAssembler(ProductoMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public ProductoResponse toModel(Producto producto) {
        ProductoResponse resp = mapper.toResponse(producto);

        // Enlace al recurso actual (detalle del producto)
        resp.add(linkTo(methodOn(ProductoController.class).getById(producto.getIdProducto()))
                .withSelfRel());

        // Enlace a la colección (productos paginados)
        resp.add(linkTo(methodOn(ProductoController.class)
                .getAll(null)) // Pageable será ignorado en el link generado
                .withRel("productos"));

        return resp;
    }
}

