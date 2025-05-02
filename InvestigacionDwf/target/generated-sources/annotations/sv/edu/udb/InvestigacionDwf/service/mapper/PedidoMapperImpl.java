package sv.edu.udb.InvestigacionDwf.service.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import sv.edu.udb.InvestigacionDwf.dto.request.PedidoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.PedidoResponse;
import sv.edu.udb.InvestigacionDwf.model.Carrito;
import sv.edu.udb.InvestigacionDwf.model.FormaPago;
import sv.edu.udb.InvestigacionDwf.model.Pedido;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-02T02:54:07-0600",
    comments = "version: 1.6.3, compiler: javac, environment: Java 23.0.2 (Oracle Corporation)"
)
@Component
public class PedidoMapperImpl extends PedidoMapper {

    @Override
    public Pedido toEntity(PedidoRequest request) {
        if ( request == null ) {
            return null;
        }

        Pedido pedido = new Pedido();

        pedido.setCarrito( carritoRepository.getReferenceById(request.getIdCarrito()) );
        pedido.setFormaPago( formaPagoRepository.getReferenceById(request.getIdFormaPago()) );

        return pedido;
    }

    @Override
    public PedidoResponse toResponse(Pedido pedido) {
        if ( pedido == null ) {
            return null;
        }

        PedidoResponse.PedidoResponseBuilder pedidoResponse = PedidoResponse.builder();

        pedidoResponse.idCarrito( pedidoCarritoIdCarrito( pedido ) );
        pedidoResponse.idFormaPago( pedidoFormaPagoIdFormaPago( pedido ) );
        pedidoResponse.idPedido( pedido.getIdPedido() );
        pedidoResponse.fechaInicio( pedido.getFechaInicio() );
        pedidoResponse.fechaFinal( pedido.getFechaFinal() );
        pedidoResponse.total( pedido.getTotal() );
        pedidoResponse.puntosTotales( pedido.getPuntosTotales() );

        return pedidoResponse.build();
    }

    private Long pedidoCarritoIdCarrito(Pedido pedido) {
        Carrito carrito = pedido.getCarrito();
        if ( carrito == null ) {
            return null;
        }
        return carrito.getIdCarrito();
    }

    private Long pedidoFormaPagoIdFormaPago(Pedido pedido) {
        FormaPago formaPago = pedido.getFormaPago();
        if ( formaPago == null ) {
            return null;
        }
        return formaPago.getIdFormaPago();
    }
}
