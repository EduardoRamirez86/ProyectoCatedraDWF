// src/main/java/sv/edu/udb/InvestigacionDwf/dto/response/HistorialPedidoResponse.java
package sv.edu.udb.InvestigacionDwf.dto.response;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;
import sv.edu.udb.InvestigacionDwf.model.enums.EstadoPedido;

import java.time.LocalDateTime;

@Data
public class HistorialPedidoResponse extends RepresentationModel<HistorialPedidoResponse> {
    private Long idHistorialPedido;
    private Long idPedido;
    private Long idUser;
    private EstadoPedido estado;
    private LocalDateTime fecha;
    private String descripcion;
}

