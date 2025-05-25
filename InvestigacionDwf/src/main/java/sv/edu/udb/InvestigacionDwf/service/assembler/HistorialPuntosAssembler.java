// src/main/java/sv/edu/udb/InvestigacionDwf/service/assembler/HistorialPuntosAssembler.java
package sv.edu.udb.InvestigacionDwf.service.assembler;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import sv.edu.udb.InvestigacionDwf.controller.HistorialPuntosController;
import sv.edu.udb.InvestigacionDwf.model.entity.HistorialPuntos;
import sv.edu.udb.InvestigacionDwf.dto.response.HistorialPuntosResponse;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class HistorialPuntosAssembler
        implements RepresentationModelAssembler<HistorialPuntos, HistorialPuntosResponse> {

    @Override
    public HistorialPuntosResponse toModel(HistorialPuntos entidad) {
        HistorialPuntosResponse m = new HistorialPuntosResponse();
        m.setIdHistorialPuntos(entidad.getIdHistorialPuntos());
        m.setIdUser(entidad.getUser().getIdUser());
        m.setIdPedido(entidad.getPedido().getIdPedido());
        m.setFecha(entidad.getFecha());
        m.setCantidadAnterior(entidad.getCantidadAnterior());
        m.setCantidadNueva(entidad.getCantidadNueva());

        // self-link
        m.add(linkTo(methodOn(HistorialPuntosController.class)
                .getById(entidad.getIdHistorialPuntos()))
                .withSelfRel()
        );

        // link a la colección paginada: pasamos null en ambos argumentos
        m.add(linkTo(methodOn(HistorialPuntosController.class)
                .getAll(
                        (Pageable) null,
                        (PagedResourcesAssembler<HistorialPuntos>) null
                ))
                .withRel("historial-puntos")
        );

        return m;
    }
}



