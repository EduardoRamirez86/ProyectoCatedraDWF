package sv.edu.udb.InvestigacionDwf.service.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import sv.edu.udb.InvestigacionDwf.dto.request.ProductoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.ProductoResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.Producto;
import sv.edu.udb.InvestigacionDwf.repository.TipoProductoRepository;
import java.util.List;
import java.util.Objects;


@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = Objects.class // ¡Añade esta línea!
)
public abstract class ProductoMapper {

    @Autowired
    protected TipoProductoRepository tipoProductoRepository;

    /**
     * Mapea un ProductoRequest a una entidad Producto.
     * Si 'idTipoProducto' en el request no es nulo, busca y asigna el TipoProducto correspondiente.
     *
     * @param request El DTO de solicitud de producto (ProductoRequest).
     * @return La entidad Producto mapeada.
     */
    @Mapping(target = "tipoProducto", expression =
            "java(Objects.nonNull(request.getIdTipoProducto()) ? tipoProductoRepository.getReferenceById(request.getIdTipoProducto()) : null)")
    public abstract Producto toEntity(ProductoRequest request);

    /**
     * Mapea una entidad Producto a un ProductoResponse.
     * Extrae el ID y el nombre del TipoProducto asociado para el DTO de respuesta.
     *
     * @param producto La entidad Producto.
     * @return El DTO de respuesta ProductoResponse.
     */
    @Mapping(source = "tipoProducto.idTipoProducto", target = "idTipoProducto")
    @Mapping(source = "tipoProducto.tipo", target = "nombreTipo")
    public abstract ProductoResponse toResponse(Producto producto);

    /**
     * Mapea una lista de entidades Producto a una lista de ProductoResponse.
     *
     * @param productos La lista de entidades Producto.
     * @return La lista de DTOs de respuesta ProductoResponse.
     */
    public abstract List<ProductoResponse> toResponseList(List<Producto> productos);

    /**
     * Actualiza una entidad Producto existente con los datos de un ProductoRequest.
     * Ignora las propiedades nulas en el request (estrategia NullValuePropertyMappingStrategy.IGNORE).
     * Si 'idTipoProducto' en el request no es nulo, actualiza el TipoProducto asociado de la entidad Producto.
     *
     * @param request El DTO de solicitud de producto con los datos a actualizar.
     * @param producto La entidad Producto a actualizar.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "tipoProducto", expression =
            "java(Objects.nonNull(request.getIdTipoProducto()) ? tipoProductoRepository.getReferenceById(request.getIdTipoProducto()) : null)")
    public abstract void updateEntityFromRequest(ProductoRequest request, @MappingTarget Producto producto);
}
