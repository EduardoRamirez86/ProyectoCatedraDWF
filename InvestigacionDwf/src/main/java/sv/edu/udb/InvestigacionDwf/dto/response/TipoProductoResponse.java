package sv.edu.udb.InvestigacionDwf.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TipoProductoResponse {
    private Long idTipoProducto;
    private String tipo;
    private String descripcion;
}
