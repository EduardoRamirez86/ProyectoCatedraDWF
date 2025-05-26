package sv.edu.udb.InvestigacionDwf.service.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import sv.edu.udb.InvestigacionDwf.dto.request.ProductoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.ProductoResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.Producto;
import sv.edu.udb.InvestigacionDwf.model.entity.TipoProducto;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-25T18:34:32-0600",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.14 (Azul Systems, Inc.)"
)
@Component
public class ProductoMapperImpl extends ProductoMapper {

    @Override
    public Producto toEntity(ProductoRequest request) {
        if ( request == null ) {
            return null;
        }

        Producto.ProductoBuilder producto = Producto.builder();

        producto.nombre( Objects.requireNonNull( request.getNombre() ) );
        producto.descripcion( Objects.requireNonNull( request.getDescripcion() ) );
        producto.precio( Objects.requireNonNull( request.getPrecio() ) );
        producto.costo( Objects.requireNonNull( request.getCosto() ) );
        producto.cantidad( Objects.requireNonNull( request.getCantidad() ) );
        producto.imagen( Objects.requireNonNull( request.getImagen() ) );
        producto.cantidadPuntos( Objects.requireNonNull( request.getCantidadPuntos() ) );

        producto.tipoProducto( Objects.nonNull(request.getIdTipoProducto()) ? tipoProductoRepository.getReferenceById(request.getIdTipoProducto()) : null );

        return producto.build();
    }

    @Override
    public ProductoResponse toResponse(Producto producto) {
        if ( producto == null ) {
            return null;
        }

        ProductoResponse.ProductoResponseBuilder productoResponse = ProductoResponse.builder();

        productoResponse.idTipoProducto( Objects.requireNonNull( productoTipoProductoIdTipoProducto( producto ) ) );
        productoResponse.nombreTipo( Objects.requireNonNull( productoTipoProductoTipo( producto ) ) );
        productoResponse.idProducto( Objects.requireNonNull( producto.getIdProducto() ) );
        productoResponse.nombre( Objects.requireNonNull( producto.getNombre() ) );
        productoResponse.descripcion( Objects.requireNonNull( producto.getDescripcion() ) );
        productoResponse.precio( Objects.requireNonNull( producto.getPrecio() ) );
        productoResponse.costo( Objects.requireNonNull( producto.getCosto() ) );
        productoResponse.cantidad( Objects.requireNonNull( producto.getCantidad() ) );
        productoResponse.imagen( Objects.requireNonNull( producto.getImagen() ) );
        productoResponse.cantidadPuntos( Objects.requireNonNull( producto.getCantidadPuntos() ) );
        productoResponse.fechaCreacion( Objects.requireNonNull( producto.getFechaCreacion() ) );
        productoResponse.fechaActualizacion( Objects.requireNonNull( producto.getFechaActualizacion() ) );

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
            producto.setNombre( Objects.requireNonNull( request.getNombre() ) );
        }
        if ( request.getDescripcion() != null ) {
            producto.setDescripcion( Objects.requireNonNull( request.getDescripcion() ) );
        }
        if ( request.getPrecio() != null ) {
            producto.setPrecio( Objects.requireNonNull( request.getPrecio() ) );
        }
        if ( request.getCosto() != null ) {
            producto.setCosto( Objects.requireNonNull( request.getCosto() ) );
        }
        if ( request.getCantidad() != null ) {
            producto.setCantidad( Objects.requireNonNull( request.getCantidad() ) );
        }
        if ( request.getImagen() != null ) {
            producto.setImagen( Objects.requireNonNull( request.getImagen() ) );
        }
        if ( request.getCantidadPuntos() != null ) {
            producto.setCantidadPuntos( Objects.requireNonNull( request.getCantidadPuntos() ) );
        }

        producto.setTipoProducto( Objects.nonNull(request.getIdTipoProducto()) ? tipoProductoRepository.getReferenceById(request.getIdTipoProducto()) : null );
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
