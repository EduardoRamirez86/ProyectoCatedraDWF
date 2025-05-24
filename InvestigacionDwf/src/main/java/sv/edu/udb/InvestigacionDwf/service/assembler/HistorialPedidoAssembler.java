package sv.edu.udb.InvestigacionDwf.service.assembler;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import sv.edu.udb.InvestigacionDwf.controller.HistorialPedidoController;
import sv.edu.udb.InvestigacionDwf.dto.response.HistorialPedidoResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.HistorialPedido;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class HistorialPedidoAssembler
        implements RepresentationModelAssembler<HistorialPedido, HistorialPedidoResponse> {

    @Override
    public HistorialPedidoResponse toModel(HistorialPedido e) {
        HistorialPedidoResponse dto = new HistorialPedidoResponse();
        dto.setIdHistorialPedido(e.getIdHistorialPedido());
        dto.setIdPedido(e.getPedido().getIdPedido());
        dto.setIdUser(e.getUser().getIdUser());
        dto.setEstado(e.getEstado());
        dto.setFecha(e.getFecha());
        dto.setDescripcion(e.getDescripcion());

        // Self link
        dto.add(linkTo(methodOn(HistorialPedidoController.class)
                .getById(e.getIdHistorialPedido()))
                .withSelfRel());

        // Link a la colección paginada (Pageable y PagedResourcesAssembler inyectados en el handler)
        dto.add(linkTo(methodOn(HistorialPedidoController.class)
                .getAll(null, null))  // el framework reemplaza correctamente
                .withRel("historial-pedidos"));

        return dto;
    }
}



