package sv.edu.udb.InvestigacionDwf.service.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import sv.edu.udb.InvestigacionDwf.dto.request.ProductoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.ProductoResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.Producto;
import sv.edu.udb.InvestigacionDwf.model.entity.TipoProducto;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-23T18:49:02-0600",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.13 (Amazon.com Inc.)"
)
@Component
public class ProductoMapperImpl extends ProductoMapper {

    @Override
    public Producto toEntity(ProductoRequest request) {
        if ( request == null ) {
            return null;
        }

        Producto.ProductoBuilder producto = Producto.builder();

        producto.nombre( request.getNombre() );
        producto.descripcion( request.getDescripcion() );
        producto.precio( request.getPrecio() );
        producto.costo( request.getCosto() );
        producto.cantidad( request.getCantidad() );
        producto.imagen( request.getImagen() );
        producto.cantidadPuntos( request.getCantidadPuntos() );

        producto.tipoProducto( tipoProductoRepository.getReferenceById(request.getIdTipoProducto()) );

        return producto.build();
    }

    @Override
    public ProductoResponse toResponse(Producto producto) {
        if ( producto == null ) {
            return null;
        }

        ProductoResponse.ProductoResponseBuilder productoResponse = ProductoResponse.builder();

        productoResponse.idTipoProducto( productoTipoProductoIdTipoProducto( producto ) );
        productoResponse.nombreTipo( productoTipoProductoTipo( producto ) );
        productoResponse.idProducto( producto.getIdProducto() );
        productoResponse.nombre( producto.getNombre() );
        productoResponse.descripcion( producto.getDescripcion() );
        productoResponse.precio( producto.getPrecio() );
        productoResponse.costo( producto.getCosto() );
        productoResponse.cantidad( producto.getCantidad() );
        productoResponse.imagen( producto.getImagen() );
        productoResponse.cantidadPuntos( producto.getCantidadPuntos() );
        productoResponse.fechaCreacion( producto.getFechaCreacion() );
        productoResponse.fechaActualizacion( producto.getFechaActualizacion() );

        return productoResponse.build();
    }

    @Override
    public List<ProductoResponse> toResponseList(List<Producto> productos) {
        if ( productos == null ) {
            return null;
        }

        List<ProductoResponse> list = new ArrayList<ProductoResponse>( productos.size() );
        for ( Producto producto : productos ) {
            list.add( toResponse( producto ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromRequest(ProductoRequest request, Producto producto) {
        if ( request == null ) {
            return;
        }

        if ( request.getNombre() != null ) {
            producto.setNombre( request.getNombre() );
        }
        if ( request.getDescripcion() != null ) {
            producto.setDescripcion( request.getDescripcion() );
        }
        if ( request.getPrecio() != null ) {
            producto.setPrecio( request.getPrecio() );
        }
        if ( request.getCosto() != null ) {
            producto.setCosto( request.getCosto() );
        }
        if ( request.getCantidad() != null ) {
            producto.setCantidad( request.getCantidad() );
        }
        if ( request.getImagen() != null ) {
            producto.setImagen( request.getImagen() );
        }
        if ( request.getCantidadPuntos() != null ) {
            producto.setCantidadPuntos( request.getCantidadPuntos() );
        }

        producto.setTipoProducto( request.getIdTipoProducto()!=null?tipoProductoRepository.getReferenceById(request.getIdTipoProducto()):null );
    }

    private Long productoTipoProductoIdTipoProducto(Producto producto) {
        TipoProducto tipoProducto = producto.getTipoProducto();
        if ( tipoProducto == null ) {
            return null;
        }
        return tipoProducto.getIdTipoProducto();
    }

    private String productoTipoProductoTipo(Producto producto) {
        TipoProducto tipoProducto = producto.getTipoProducto();
        if ( tipoProducto == null ) {
            return null;
        }
        return tipoProducto.getTipo();
    }
}
