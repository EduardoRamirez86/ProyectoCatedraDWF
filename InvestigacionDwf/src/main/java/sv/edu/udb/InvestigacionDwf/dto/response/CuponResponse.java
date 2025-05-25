// src/main/java/sv/edu/udb/InvestigacionDwf/dto/response/CuponResponse.java
package sv.edu.udb.InvestigacionDwf.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CuponResponse {
    private Long idCupon;
    private String codigo;
    private Double porcentajeDescuento;
    private boolean usado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaExpiracion;
}