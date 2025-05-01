package sv.edu.udb.InvestigacionDwf.service.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import sv.edu.udb.InvestigacionDwf.dto.request.ProductoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.ProductoResponse;
import sv.edu.udb.InvestigacionDwf.model.Producto;
import sv.edu.udb.InvestigacionDwf.model.TipoProducto;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-01T07:37:06-0600",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.42.0.z20250331-1358, environment: Java 21.0.6 (Eclipse Adoptium)"
)
@Component
public class ProductoMapperImpl extends ProductoMapper {

    @Override
    public Producto toEntity(ProductoRequest request) {
        if ( request == null ) {
            return null;
        }

        Producto producto = new Producto();

        producto.setCantidad( request.getCantidad() );
        producto.setCantidadPuntos( request.getCantidadPuntos() );
        producto.setCosto( request.getCosto() );
        producto.setDescripcion( request.getDescripcion() );
        producto.setImagen( request.getImagen() );
        producto.setNombre( request.getNombre() );
        producto.setPrecio( request.getPrecio() );

        producto.setTipoProducto( request.getIdTipoProducto() != null ? tipoProductoRepository.getReferenceById(request.getIdTipoProducto()) : null );

        return producto;
    }

    @Override
    public ProductoResponse toResponse(Producto producto) {
        if ( producto == null ) {
            return null;
        }

        ProductoResponse.ProductoResponseBuilder productoResponse = ProductoResponse.builder();

        productoResponse.idTipoProducto( productoTipoProductoIdTipoProducto( producto ) );
        productoResponse.cantidad( producto.getCantidad() );
        productoResponse.cantidadPuntos( producto.getCantidadPuntos() );
        productoResponse.costo( producto.getCosto() );
        productoResponse.descripcion( producto.getDescripcion() );
        productoResponse.idProducto( producto.getIdProducto() );
        productoResponse.imagen( producto.getImagen() );
        productoResponse.nombre( producto.getNombre() );
        productoResponse.precio( producto.getPrecio() );

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

        if ( request.getCantidad() != null ) {
            producto.setCantidad( request.getCantidad() );
        }
        if ( request.getCantidadPuntos() != null ) {
            producto.setCantidadPuntos( request.getCantidadPuntos() );
        }
        if ( request.getCosto() != null ) {
            producto.setCosto( request.getCosto() );
        }
        if ( request.getDescripcion() != null ) {
            producto.setDescripcion( request.getDescripcion() );
        }
        if ( request.getImagen() != null ) {
            producto.setImagen( request.getImagen() );
        }
        if ( request.getNombre() != null ) {
            producto.setNombre( request.getNombre() );
        }
        if ( request.getPrecio() != null ) {
            producto.setPrecio( request.getPrecio() );
        }

        producto.setTipoProducto( request.getIdTipoProducto() != null ? tipoProductoRepository.getReferenceById(request.getIdTipoProducto()) : null );
    }

    private Long productoTipoProductoIdTipoProducto(Producto producto) {
        TipoProducto tipoProducto = producto.getTipoProducto();
        if ( tipoProducto == null ) {
            return null;
        }
        return tipoProducto.getIdTipoProducto();
    }
}
