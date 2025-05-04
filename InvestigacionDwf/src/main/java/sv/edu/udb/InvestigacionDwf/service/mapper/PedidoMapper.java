// src/main/java/sv/edu/udb/InvestigacionDwf/service/mapper/PedidoMapper.java
package sv.edu.udb.InvestigacionDwf.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import sv.edu.udb.InvestigacionDwf.dto.request.PedidoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.PedidoResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.Pedido;
import sv.edu.udb.InvestigacionDwf.repository.CarritoRepository;
import sv.edu.udb.InvestigacionDwf.repository.FormaPagoRepository;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class PedidoMapper {

    @Autowired
    protected CarritoRepository carritoRepository;

    @Autowired
    protected FormaPagoRepository formaPagoRepository;

    @Mapping(target = "carrito", expression = "java(carritoRepository.getReferenceById(request.getIdCarrito()))")
    @Mapping(target = "formaPago", expression = "java(formaPagoRepository.getReferenceById(request.getIdFormaPago()))")
    public abstract Pedido toEntity(PedidoRequest request);

    @Mapping(target = "idCarrito", source = "carrito.idCarrito")
    @Mapping(target = "idFormaPago", source = "formaPago.idFormaPago")
    public abstract PedidoResponse toResponse(Pedido pedido);
}

