// src/main/java/sv/edu/udb/InvestigacionDwf/service/mapper/PedidoMapper.java
package sv.edu.udb.InvestigacionDwf.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import sv.edu.udb.InvestigacionDwf.dto.request.PedidoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.PedidoResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.Pedido;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PedidoMapper {

    @Mapping(source = "idCarrito", target = "carrito.idCarrito")
    @Mapping(source = "tipoPago", target = "tipoPago")
    @Mapping(source = "idDireccion", target = "direccion.idDireccion")
    Pedido toEntity(PedidoRequest request);

    @Mapping(source = "carrito.idCarrito", target = "idCarrito")
    @Mapping(source = "tipoPago", target = "tipoPago")
    @Mapping(source = "estado", target = "estado")
    @Mapping(target = "idDireccion", expression = "java(pedido.getDireccion() != null ? pedido.getDireccion().getIdDireccion() : null)")

    // *** AÑADE ESTAS LÍNEAS PARA MAPEAR LOS DETALLES DE LA DIRECCIÓN ***
    @Mapping(source = "direccion.alias", target = "aliasDireccion")
    @Mapping(source = "direccion.calle", target = "calleDireccion")
    @Mapping(source = "direccion.ciudad", target = "ciudadDireccion")
    @Mapping(source = "direccion.departamento", target = "departamentoDireccion")
        // *******************************************************************

    PedidoResponse toResponse(Pedido pedido);
}



