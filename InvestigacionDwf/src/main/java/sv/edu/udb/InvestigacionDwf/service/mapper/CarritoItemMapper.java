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
        uses = { Objects.class, ProductoMapper.class }) // Añadimos ProductoMapper para el mapeo anidado
public abstract class CarritoItemMapper {

    @Autowired
    protected CarritoRepository carritoRepository;

    @Autowired
    protected ProductoRepository productoRepository;

    /**
     * Convierte un DTO de solicitud (CarritoItemRequest) a una entidad CarritoItem.
     * Usa getReferenceById para una asignación eficiente de claves foráneas.
     */
    @Mapping(target = "carrito", expression = "java(Objects.nonNull(request.getIdCarrito()) ? carritoRepository.getReferenceById(request.getIdCarrito()) : null)")
    @Mapping(target = "producto", expression = "java(Objects.nonNull(request.getIdProducto()) ? productoRepository.getReferenceById(request.getIdProducto()) : null)")
    public abstract CarritoItem toEntity(CarritoItemRequest request);

    // --- 👇👇👇 ¡AQUÍ ESTÁ LA LÓGICA BLINDADA! 👇👇👇 ---
    /**
     * Convierte una entidad CarritoItem a un DTO de respuesta (CarritoItemResponse).
     * Es a prueba de nulos. Si `carrito` o `producto` son nulos en la entidad,
     * los IDs correspondientes en la respuesta serán nulos, evitando el NullPointerException.
     */
    @Mapping(target = "idCarrito", source = "carrito.idCarrito")
    @Mapping(target = "idProducto", source = "producto.idProducto")
    // Mapea la entidad anidada Producto a su DTO ProductResponse.
    // MapStruct es lo suficientemente inteligente para manejar si `item.getProducto()` es null.
    @Mapping(target = "producto", source = "producto")
    public abstract CarritoItemResponse toResponse(CarritoItem item);
    // --- ----------------------------------------------- ---


    /**
     * Actualiza una entidad CarritoItem existente con los datos de un DTO de solicitud.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "carrito", expression = "java(Objects.nonNull(request.getIdCarrito()) ? carritoRepository.getReferenceById(request.getIdCarrito()) : targetItem.getCarrito())")
    @Mapping(target = "producto", expression = "java(Objects.nonNull(request.getIdProducto()) ? productoRepository.getReferenceById(request.getIdProducto()) : targetItem.getProducto())")
    public abstract void updateEntityFromRequest(CarritoItemRequest request, @MappingTarget CarritoItem targetItem);
}

