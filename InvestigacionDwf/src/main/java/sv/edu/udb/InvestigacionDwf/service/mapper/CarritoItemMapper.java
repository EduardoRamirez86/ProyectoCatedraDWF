// src/main/java/sv/edu/udb/InvestigacionDwf/service/mapper/CarritoItemMapper.java
package sv.edu.udb.InvestigacionDwf.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import sv.edu.udb.InvestigacionDwf.dto.request.CarritoItemRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.CarritoItemResponse;
import sv.edu.udb.InvestigacionDwf.model.CarritoItem;
import sv.edu.udb.InvestigacionDwf.repository.CarritoRepository;
import sv.edu.udb.InvestigacionDwf.repository.ProductoRepository;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class CarritoItemMapper {

    @Autowired
    protected CarritoRepository carritoRepository;

    @Autowired
    protected ProductoRepository productoRepository;

    @Mapping(target = "carrito", expression = "java(carritoRepository.getReferenceById(request.getIdCarrito()))")
    @Mapping(target = "producto", expression = "java(productoRepository.getReferenceById(request.getIdProducto()))")
    public abstract CarritoItem toEntity(CarritoItemRequest request);

    @Mapping(target = "idCarrito", source = "carrito.idCarrito")
    @Mapping(target = "idProducto", source = "producto.idProducto")
    public abstract CarritoItemResponse toResponse(CarritoItem item);
}
