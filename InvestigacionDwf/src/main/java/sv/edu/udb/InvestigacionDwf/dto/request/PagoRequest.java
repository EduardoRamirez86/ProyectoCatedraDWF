// src/main/java/sv/edu/udb/InvestigacionDwf/dto/request/PagoRequest.java
package sv.edu.udb.InvestigacionDwf.dto.request;

import lombok.Data;
import sv.edu.udb.InvestigacionDwf.model.entity.User;

@Data
public class PagoRequest {
    private User usuario;
    private String detallesPago;
}
