// src/main/java/sv/edu/udb/InvestigacionDwf/model/hal/HistorialPuntosModel.java
package sv.edu.udb.InvestigacionDwf.model.hal;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Data
public class HistorialPuntosModel extends RepresentationModel<HistorialPuntosModel> {
    private Long           idHistorialPuntos;
    private Long           idUser;
    private Long           idPedido;
    private LocalDateTime  fecha;
    private Integer        cantidadAnterior;
    private Integer        cantidadNueva;
}


