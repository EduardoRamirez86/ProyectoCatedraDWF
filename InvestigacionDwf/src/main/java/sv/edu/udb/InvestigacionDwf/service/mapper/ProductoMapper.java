package sv.edu.udb.InvestigacionDwf.service.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import sv.edu.udb.InvestigacionDwf.dto.request.ProductoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.ProductoResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.Producto;
import sv.edu.udb.InvestigacionDwf.repository.TipoProductoRepository;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ProductoMapper {

    @Autowired
    protected TipoProductoRepository tipoProductoRepository;

    @Mapping(target = "tipoProducto", expression =
            "java(tipoProductoRepository.getReferenceById(request.getIdTipoProducto()))")
    public abstract Producto toEntity(ProductoRequest request);

    @Mapping(source = "tipoProducto.idTipoProducto", target = "idTipoProducto")
    @Mapping(source = "tipoProducto.tipo", target = "nombreTipo")
    public abstract ProductoResponse toResponse(Producto producto);

    public abstract List<ProductoResponse> toResponseList(List<Producto> productos);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "tipoProducto", expression =
            "java(request.getIdTipoProducto()!=null?tipoProductoRepository.getReferenceById(request.getIdTipoProducto()):null)")
    public abstract void updateEntityFromRequest(ProductoRequest request, @MappingTarget Producto producto);
}



