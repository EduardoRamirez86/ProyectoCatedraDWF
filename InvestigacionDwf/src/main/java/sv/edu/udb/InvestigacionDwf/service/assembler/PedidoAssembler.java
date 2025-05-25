package sv.edu.udb.InvestigacionDwf.service.assembler;

import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import sv.edu.udb.InvestigacionDwf.controller.PedidoController;
import sv.edu.udb.InvestigacionDwf.dto.response.PedidoResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.Pedido;
import sv.edu.udb.InvestigacionDwf.service.mapper.PedidoMapper;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class PedidoAssembler implements RepresentationModelAssembler<Pedido, PedidoResponse> {

    private final PedidoMapper pedidoMapper;

    public PedidoAssembler(PedidoMapper pedidoMapper) {
        this.pedidoMapper = pedidoMapper;
    }

    @Override
    public PedidoResponse toModel(Pedido pedido) {
        PedidoResponse response = pedidoMapper.toResponse(pedido);

        // self link (usa el GET /auth/pedido/{id})
        response.add(linkTo(methodOn(PedidoController.class)
                .getById(pedido.getIdPedido()))
                .withSelfRel());

        // enlace a la lista de pedidos del usuario
        // Como Pageable y PagedResourcesAssembler no pueden ser null, usamos una URI manual o omitimos este link
        response.add(linkTo(PedidoController.class)
                .slash("user")
                .slash(pedido.getCarrito().getUser().getIdUser())
                .withRel("pedidos-usuario"));

        return response;
    }
}
