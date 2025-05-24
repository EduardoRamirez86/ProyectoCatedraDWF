// src/main/java/sv/edu/udb/InvestigacionDwf/dto/response/HistorialPuntosModel.java
package sv.edu.udb.InvestigacionDwf.dto.response;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Data
public class HistorialPuntosResponse extends RepresentationModel<HistorialPuntosResponse> {
    private Long           idHistorialPuntos;
    private Long           idUser;
    private Long           idPedido;
    private LocalDateTime  fecha;
    private Integer        cantidadAnterior;
    private Integer        cantidadNueva;
}


