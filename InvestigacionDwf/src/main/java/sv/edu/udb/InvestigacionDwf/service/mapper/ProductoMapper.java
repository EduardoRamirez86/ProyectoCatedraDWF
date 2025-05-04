package sv.edu.udb.InvestigacionDwf.service.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.mapstruct.Mapping;
import sv.edu.udb.InvestigacionDwf.dto.request.ProductoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.ProductoResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.Producto;
import sv.edu.udb.InvestigacionDwf.repository.TipoProductoRepository;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class ProductoMapper {

    @Autowired
    protected TipoProductoRepository tipoProductoRepository;

    // Del DTO (request) a la entidad (Producto)
    @Mapping(target = "tipoProducto", expression =
            "java(request.getIdTipoProducto() != null ? " +
                    "tipoProductoRepository.getReferenceById(request.getIdTipoProducto()) : null)")
    public abstract Producto toEntity(ProductoRequest request);

    // De la entidad a DTO (response)
    @Mapping(target = "idTipoProducto", source = "tipoProducto.idTipoProducto")
    @Mapping(target = "nombreTipo", source = "tipoProducto.tipo") // Agrega este mapeo
    public abstract ProductoResponse toResponse(Producto producto);


    // Lista
    public abstract List<ProductoResponse> toResponseList(List<Producto> productos);

    // Actualizar entidad desde el request
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "tipoProducto", expression =
            "java(request.getIdTipoProducto() != null ? " +
                    "tipoProductoRepository.getReferenceById(request.getIdTipoProducto()) : null)")
    public abstract void updateEntityFromRequest(
            ProductoRequest request,
            @MappingTarget Producto producto
    );
}


