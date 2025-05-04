package sv.edu.udb.InvestigacionDwf.service.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import sv.edu.udb.InvestigacionDwf.dto.request.CarritoItemRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.CarritoItemResponse;
import sv.edu.udb.InvestigacionDwf.dto.response.ProductoResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.Carrito;
import sv.edu.udb.InvestigacionDwf.model.entity.CarritoItem;
import sv.edu.udb.InvestigacionDwf.model.entity.Producto;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-03T20:41:23-0600",
    comments = "version: 1.6.3, compiler: javac, environment: Java 23.0.2 (Oracle Corporation)"
)
@Component
public class CarritoItemMapperImpl extends CarritoItemMapper {

    @Override
    public CarritoItem toEntity(CarritoItemRequest request) {
        if ( request == null ) {
            return null;
        }

        CarritoItem carritoItem = new CarritoItem();

        carritoItem.setCantidad( request.getCantidad() );

        carritoItem.setCarrito( carritoRepository.getReferenceById(request.getIdCarrito()) );
        carritoItem.setProducto( productoRepository.getReferenceById(request.getIdProducto()) );

        return carritoItem;
    }

    @Override
    public CarritoItemResponse toResponse(CarritoItem item) {
        if ( item == null ) {
            return null;
        }

        CarritoItemResponse.CarritoItemResponseBuilder carritoItemResponse = CarritoItemResponse.builder();

        carritoItemResponse.idCarrito( itemCarritoIdCarrito( item ) );
        carritoItemResponse.idProducto( itemProductoIdProducto( item ) );
        carritoItemResponse.idCarritoItem( item.getIdCarritoItem() );
        carritoItemResponse.cantidad( item.getCantidad() );
        carritoItemResponse.producto( productoToProductoResponse( item.getProducto() ) );

        return carritoItemResponse.build();
    }

    private Long itemCarritoIdCarrito(CarritoItem carritoItem) {
        Carrito carrito = carritoItem.getCarrito();
        if ( carrito == null ) {
            return null;
        }
        return carrito.getIdCarrito();
    }

    private Long itemProductoIdProducto(CarritoItem carritoItem) {
        Producto producto = carritoItem.getProducto();
        if ( producto == null ) {
            return null;
        }
        return producto.getIdProducto();
    }

    protected ProductoResponse productoToProductoResponse(Producto producto) {
        if ( producto == null ) {
            return null;
        }

        ProductoResponse.ProductoResponseBuilder productoResponse = ProductoResponse.builder();

        productoResponse.idProducto( producto.getIdProducto() );
        productoResponse.nombre( producto.getNombre() );
        productoResponse.descripcion( producto.getDescripcion() );
        productoResponse.precio( producto.getPrecio() );
        productoResponse.costo( producto.getCosto() );
        productoResponse.cantidad( producto.getCantidad() );
        productoResponse.imagen( producto.getImagen() );
        productoResponse.cantidadPuntos( producto.getCantidadPuntos() );

        return productoResponse.build();
    }
}
