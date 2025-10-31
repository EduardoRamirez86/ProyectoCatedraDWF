package sv.edu.udb.InvestigacionDwf.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PedidoItemDto {
    private Long idProducto;
    private String nombreProducto;
    private int cantidad;
    private BigDecimal precioUnitario;
}

