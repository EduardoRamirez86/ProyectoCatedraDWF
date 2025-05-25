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
        // como el controlador define getByUser(idUser, Pageable, PagedResourcesAssembler)
        // le pasamos tres args: id, null para pageable, null para assembler
        response.add(linkTo(methodOn(PedidoController.class)
                .getByUser(
                        pedido.getCarrito().getUser().getIdUser(),
                        /* pageable */ null,
                        /* assembler */ null
                ))
                .withRel("pedidos-usuario"));

        return response;
    }
}
