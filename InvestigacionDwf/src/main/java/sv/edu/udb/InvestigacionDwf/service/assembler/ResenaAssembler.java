// src/main/java/sv/edu/udb/InvestigacionDwf/service/assembler/ResenaAssembler.java
package sv.edu.udb.InvestigacionDwf.service.assembler;

import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import sv.edu.udb.InvestigacionDwf.controller.ResenaController;
import sv.edu.udb.InvestigacionDwf.dto.response.ResenaResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.Resena;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ResenaAssembler implements RepresentationModelAssembler<Resena, ResenaResponse> {

    @Override
    public ResenaResponse toModel(Resena e) {
        ResenaResponse dto = ResenaResponse.builder()
                .idResena(e.getIdResena())
                .username(e.getUser().getUsername())
                .productoNombre(e.getProducto().getNombre())
                .comentario(e.getComentario())
                .fecha(e.getFecha())
                .rating(e.getRating())
                .build();

        // enlace self
        dto.add(linkTo(methodOn(ResenaController.class)
                .getById(e.getIdResena()))
                .withSelfRel());

        // enlace paginado (sólo Pageable)
        dto.add(linkTo(methodOn(ResenaController.class)
                .getAll((Pageable) null))
                .withRel("resenas"));

        return dto;
    }
}


