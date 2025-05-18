package sv.edu.udb.InvestigacionDwf.service.mapper;

import org.springframework.stereotype.Component;
import sv.edu.udb.InvestigacionDwf.dto.request.TipoProductoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.TipoProductoResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.TipoProducto;

@Component
public class TipoProductoMapper {

    public TipoProducto toEntity(TipoProductoRequest request) {
        return TipoProducto.builder()
                .tipo(request.getTipo())
                .descripcion(request.getDescripcion())
                .build();
    }

    public TipoProductoResponse toResponse(TipoProducto entity) {
        return TipoProductoResponse.builder()
                .idTipoProducto(entity.getIdTipoProducto())
                .tipo(entity.getTipo())
                .descripcion(entity.getDescripcion())
                .build();
    }
}

