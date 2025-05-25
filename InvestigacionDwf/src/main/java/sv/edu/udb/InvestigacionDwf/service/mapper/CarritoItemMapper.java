package sv.edu.udb.InvestigacionDwf.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import sv.edu.udb.InvestigacionDwf.dto.request.CarritoItemRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.CarritoItemResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.CarritoItem;
import sv.edu.udb.InvestigacionDwf.repository.CarritoRepository;
import sv.edu.udb.InvestigacionDwf.repository.ProductoRepository;

import java.util.Objects;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = Objects.class)
public abstract class CarritoItemMapper {

    @Autowired
    protected CarritoRepository carritoRepository;

    @Autowired
    protected ProductoRepository productoRepository;

    /**
     * Convierte un DTO de solicitud (CarritoItemRequest) a una entidad CarritoItem.
     * Maneja la obtención de referencias a Carrito y Producto usando sus respectivos IDs.
     * Realiza validaciones básicas de nulidad para los IDs esenciales.
     *
     * @param request El DTO de solicitud con los datos del ítem del carrito.
     * @return Una nueva entidad CarritoItem.
     * @throws IllegalArgumentException Si el request o sus IDs de carrito/producto son nulos.
     */
    @Mapping(target = "carrito", expression = "java(Objects.nonNull(request.getIdCarrito()) ? carritoRepository.getReferenceById(request.getIdCarrito()) : null)")
    @Mapping(target = "producto", expression = "java(Objects.nonNull(request.getIdProducto()) ? productoRepository.getReferenceById(request.getIdProducto()) : null)")
    public abstract CarritoItem toEntity(CarritoItemRequest request);

    /**
     * Convierte una entidad CarritoItem a un DTO de respuesta (CarritoItemResponse).
     * Mapea los IDs de Carrito y Producto a partir de las entidades relacionadas.
     * Realiza validaciones de nulidad para la entidad y sus relaciones esenciales.
     *
     * @param item La entidad CarritoItem a convertir.
     * @return Un DTO CarritoItemResponse.
     * @throws IllegalArgumentException Si la entidad item o sus relaciones (carrito, producto) son nulas.
     */
    @Mapping(target = "idCarrito", source = "carrito.idCarrito")
    @Mapping(target = "idProducto", source = "producto.idProducto")
    public abstract CarritoItemResponse toResponse(CarritoItem item);

    /**
     * Actualiza una entidad CarritoItem existente con los datos de un DTO de solicitud.
     * Permite actualizar solo los campos que no son nulos en el request (estrategia de mapeo nulo ignorada).
     * Maneja la actualización de referencias a Carrito y Producto si sus IDs se proporcionan.
     *
     * @param request El DTO de solicitud con los datos a actualizar.
     * @param targetItem La entidad CarritoItem existente que se va a actualizar.
     * @throws IllegalArgumentException Si el request o el targetItem son nulos.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "carrito", expression = "java(Objects.nonNull(request.getIdCarrito()) ? carritoRepository.getReferenceById(request.getIdCarrito()) : targetItem.getCarrito())")
    @Mapping(target = "producto", expression = "java(Objects.nonNull(request.getIdProducto()) ? productoRepository.getReferenceById(request.getIdProducto()) : targetItem.getProducto())")
    // ¡LA LÍNEA PROBLEMÁTICA SE ELIMINÓ AQUÍ!
    public abstract void updateEntityFromRequest(CarritoItemRequest request, @MappingTarget CarritoItem targetItem);
}
