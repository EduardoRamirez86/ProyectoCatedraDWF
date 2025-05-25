// src/main/java/sv/edu/udb/InvestigacionDwf/dto/request/DireccionRequest.java
package sv.edu.udb.InvestigacionDwf.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DireccionRequest {
    private String alias;
    private String calle;
    private String ciudad;
    private String departamento;
    private Double latitud;
    private Double longitud;
}

