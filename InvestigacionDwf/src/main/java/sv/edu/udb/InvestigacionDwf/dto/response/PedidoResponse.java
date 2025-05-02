package sv.edu.udb.InvestigacionDwf.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// PedidoResponse.java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoResponse {
    private Long idPedido;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFinal;
    private BigDecimal total;
    private Integer puntosTotales;
    private Long idCarrito;
    private Long idFormaPago;
}
