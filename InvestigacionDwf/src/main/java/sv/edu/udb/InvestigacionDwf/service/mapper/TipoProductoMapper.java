package sv.edu.udb.InvestigacionDwf.service.mapper;

import org.springframework.stereotype.Component;
import sv.edu.udb.InvestigacionDwf.dto.request.TipoProductoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.TipoProductoResponse;
import sv.edu.udb.InvestigacionDwf.model.TipoProducto;

@Component
public class TipoProductoMapper {

    public TipoProducto toEntity(TipoProductoRequest request) {
        TipoProducto tipoProducto = new TipoProducto();
        tipoProducto.setTipo(request.getTipo());
        tipoProducto.setDescripcion(request.getDescripcion());
        return tipoProducto;
    }

    public TipoProductoResponse toResponse(TipoProducto entity) {
        TipoProductoResponse response = new TipoProductoResponse();
        response.setIdTipoProducto(entity.getIdTipoProducto());
        response.setTipo(entity.getTipo());
        response.setDescripcion(entity.getDescripcion());
        return response;
    }
}

