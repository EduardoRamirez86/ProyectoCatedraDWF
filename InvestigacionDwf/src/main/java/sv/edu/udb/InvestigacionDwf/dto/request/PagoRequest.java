// src/main/java/sv/edu/udb/InvestigacionDwf/dto/request/PagoRequest.java
package sv.edu.udb.InvestigacionDwf.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import sv.edu.udb.InvestigacionDwf.model.entity.User;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PagoRequest {
    private User usuario;
    private String detallesPago;
}
