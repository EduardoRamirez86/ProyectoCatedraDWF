package sv.edu.udb.InvestigacionDwf.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// PedidoRequest.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequest {
    private Long idCarrito;
    private Long idFormaPago;
}
