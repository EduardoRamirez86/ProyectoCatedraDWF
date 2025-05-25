// src/main/java/sv/edu/udb/InvestigacionDwf/dto/response/PedidoResponse.java
package sv.edu.udb.InvestigacionDwf.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sv.edu.udb.InvestigacionDwf.model.enums.EstadoPedido;
import sv.edu.udb.InvestigacionDwf.model.enums.TipoPago;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoResponse extends RepresentationModel<PedidoResponse> {
    private Long idPedido;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFinal;
    private BigDecimal total;
    private Integer puntosTotales;
    private Long idCarrito;
    private TipoPago tipoPago;
    private EstadoPedido estado;
    private Long idDireccion;
    private String aliasDireccion;
    private String calleDireccion;
    private String ciudadDireccion;
    private String departamentoDireccion;
}

