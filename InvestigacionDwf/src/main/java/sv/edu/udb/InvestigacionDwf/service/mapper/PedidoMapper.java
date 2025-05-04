// src/main/java/sv/edu/udb/InvestigacionDwf/service/mapper/PedidoMapper.java
package sv.edu.udb.InvestigacionDwf.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sv.edu.udb.InvestigacionDwf.dto.request.PedidoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.PedidoResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.Pedido;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface PedidoMapper {

    @Mapping(source = "idCarrito", target = "carrito.idCarrito")
    @Mapping(source = "tipoPago", target = "tipoPago")
    Pedido toEntity(PedidoRequest request);

    @Mapping(source = "carrito.idCarrito", target = "idCarrito")
    @Mapping(source = "tipoPago", target = "tipoPago")
    @Mapping(source = "estado", target = "estado")
    PedidoResponse toResponse(Pedido pedido);
}


