// src/main/java/sv/edu/udb/InvestigacionDwf/dto/request/DireccionRequest.java
package sv.edu.udb.InvestigacionDwf.dto.request;

import lombok.Data;

@Data
public class DireccionRequest {
    private String alias;
    private String calle;
    private String ciudad;
    private String departamento;
    private Double latitud;
    private Double longitud;
}

