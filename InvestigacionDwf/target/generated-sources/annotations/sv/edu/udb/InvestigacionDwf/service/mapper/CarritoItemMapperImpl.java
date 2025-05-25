package sv.edu.udb.InvestigacionDwf.service.mapper;

import java.util.Objects;
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
    date = "2025-05-24T08:09:43-0600",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class CarritoItemMapperImpl extends CarritoItemMapper {

    @Override
    public CarritoItem toEntity(CarritoItemRequest request) {
        if ( request == null ) {
            return null;
        }

        CarritoItem carritoItem = new CarritoItem();

        carritoItem.setCantidad( Objects.requireNonNull( request.getCantidad() ) );

        carritoItem.setCarrito( Objects.nonNull(request.getIdCarrito()) ? carritoRepository.getReferenceById(request.getIdCarrito()) : null );
        carritoItem.setProducto( Objects.nonNull(request.getIdProducto()) ? productoRepository.getReferenceById(request.getIdProducto()) : null );

        return carritoItem;
    }

    @Override
    public CarritoItemResponse toResponse(CarritoItem item) {
        if ( item == null ) {
            return null;
        }

        CarritoItemResponse.CarritoItemResponseBuilder carritoItemResponse = CarritoItemResponse.builder();

        carritoItemResponse.idCarrito( Objects.requireNonNull( itemCarritoIdCarrito( item ) ) );
        carritoItemResponse.idProducto( Objects.requireNonNull( itemProductoIdProducto( item ) ) );
        carritoItemResponse.idCarritoItem( Objects.requireNonNull( item.getIdCarritoItem() ) );
        carritoItemResponse.cantidad( Objects.requireNonNull( item.getCantidad() ) );
        carritoItemResponse.producto( productoToProductoResponse( item.getProducto() ) );

        return carritoItemResponse.build();
    }

    @Override
    public void updateEntityFromRequest(CarritoItemRequest request, CarritoItem targetItem) {
        if ( request == null ) {
            return;
        }

        if ( request.getCantidad() != null ) {
            targetItem.setCantidad( Objects.requireNonNull( request.getCantidad() ) );
        }

        targetItem.setCarrito( Objects.nonNull(request.getIdCarrito()) ? carritoRepository.getReferenceById(request.getIdCarrito()) : targetItem.getCarrito() );
        targetItem.setProducto( Objects.nonNull(request.getIdProducto()) ? productoRepository.getReferenceById(request.getIdProducto()) : targetItem.getProducto() );
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
}
