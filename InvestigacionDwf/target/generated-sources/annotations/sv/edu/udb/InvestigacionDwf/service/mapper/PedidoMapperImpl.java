package sv.edu.udb.InvestigacionDwf.service.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import sv.edu.udb.InvestigacionDwf.dto.request.PedidoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.PedidoResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.Carrito;
import sv.edu.udb.InvestigacionDwf.model.entity.Direccion;
import sv.edu.udb.InvestigacionDwf.model.entity.Pedido;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-23T21:07:47-0600",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.15 (Eclipse Adoptium)"
)
@Component
public class PedidoMapperImpl implements PedidoMapper {

    @Override
    public Pedido toEntity(PedidoRequest request) {
        if ( request == null ) {
            return null;
        }

        Pedido.PedidoBuilder pedido = Pedido.builder();

        pedido.carrito( pedidoRequestToCarrito( request ) );
        pedido.direccion( pedidoRequestToDireccion( request ) );
        pedido.tipoPago( request.getTipoPago() );

        return pedido.build();
    }

    @Override
    public PedidoResponse toResponse(Pedido pedido) {
        if ( pedido == null ) {
            return null;
        }

        PedidoResponse.PedidoResponseBuilder pedidoResponse = PedidoResponse.builder();

        pedidoResponse.idCarrito( pedidoCarritoIdCarrito( pedido ) );
        pedidoResponse.tipoPago( pedido.getTipoPago() );
        pedidoResponse.estado( pedido.getEstado() );
        pedidoResponse.aliasDireccion( pedidoDireccionAlias( pedido ) );
        pedidoResponse.calleDireccion( pedidoDireccionCalle( pedido ) );
        pedidoResponse.ciudadDireccion( pedidoDireccionCiudad( pedido ) );
        pedidoResponse.departamentoDireccion( pedidoDireccionDepartamento( pedido ) );
        pedidoResponse.idPedido( pedido.getIdPedido() );
        pedidoResponse.fechaInicio( pedido.getFechaInicio() );
        pedidoResponse.fechaFinal( pedido.getFechaFinal() );
        pedidoResponse.total( pedido.getTotal() );
        pedidoResponse.puntosTotales( pedido.getPuntosTotales() );

        pedidoResponse.idDireccion( pedido.getDireccion() != null ? pedido.getDireccion().getIdDireccion() : null );

        return pedidoResponse.build();
    }

    protected Carrito pedidoRequestToCarrito(PedidoRequest pedidoRequest) {
        if ( pedidoRequest == null ) {
            return null;
        }

        Carrito.CarritoBuilder carrito = Carrito.builder();

        carrito.idCarrito( pedidoRequest.getIdCarrito() );

        return carrito.build();
    }

    protected Direccion pedidoRequestToDireccion(PedidoRequest pedidoRequest) {
        if ( pedidoRequest == null ) {
            return null;
        }

        Direccion.DireccionBuilder direccion = Direccion.builder();

        direccion.idDireccion( pedidoRequest.getIdDireccion() );

        return direccion.build();
    }

    private Long pedidoCarritoIdCarrito(Pedido pedido) {
        Carrito carrito = pedido.getCarrito();
        if ( carrito == null ) {
            return null;
        }
        return carrito.getIdCarrito();
    }

    private String pedidoDireccionAlias(Pedido pedido) {
        Direccion direccion = pedido.getDireccion();
        if ( direccion == null ) {
            return null;
        }
        return direccion.getAlias();
    }

    private String pedidoDireccionCalle(Pedido pedido) {
        Direccion direccion = pedido.getDireccion();
        if ( direccion == null ) {
            return null;
        }
        return direccion.getCalle();
    }

    private String pedidoDireccionCiudad(Pedido pedido) {
        Direccion direccion = pedido.getDireccion();
        if ( direccion == null ) {
            return null;
        }
        return direccion.getCiudad();
    }

    private String pedidoDireccionDepartamento(Pedido pedido) {
        Direccion direccion = pedido.getDireccion();
        if ( direccion == null ) {
            return null;
        }
        return direccion.getDepartamento();
    }
}
