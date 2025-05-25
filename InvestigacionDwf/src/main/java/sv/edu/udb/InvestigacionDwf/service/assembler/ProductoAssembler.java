// src/main/java/sv/edu/udb/InvestigacionDwf/service/assembler/ProductoAssembler.java
package sv.edu.udb.InvestigacionDwf.service.assembler;

import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import sv.edu.udb.InvestigacionDwf.controller.ProductoController;
import sv.edu.udb.InvestigacionDwf.dto.response.ProductoResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.Producto;
import sv.edu.udb.InvestigacionDwf.service.mapper.ProductoMapper;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ProductoAssembler implements RepresentationModelAssembler<Producto, ProductoResponse> {

    private final ProductoMapper mapper;

    public ProductoAssembler(ProductoMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public ProductoResponse toModel(Producto producto) {
        ProductoResponse response = mapper.toResponse(producto);

        // self link
        response.add(linkTo(methodOn(ProductoController.class)
                .getById(producto.getIdProducto()))
                .withSelfRel());

        // enlace a la lista paginada de productos
        response.add(linkTo(ProductoController.class)
                .slash("all")
                .withRel("productos"));

        return response;
    }
}

