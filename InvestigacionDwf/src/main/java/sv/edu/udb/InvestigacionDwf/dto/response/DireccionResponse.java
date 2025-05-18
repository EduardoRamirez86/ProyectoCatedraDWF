// src/main/java/sv/edu/udb/InvestigacionDwf/dto/response/DireccionResponse.java
package sv.edu.udb.InvestigacionDwf.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DireccionResponse {
    private Long idDireccion;
    private String alias;
    private String calle;
    private String ciudad;
    private String departamento;
    private Double latitud;
    private Double longitud;
}

