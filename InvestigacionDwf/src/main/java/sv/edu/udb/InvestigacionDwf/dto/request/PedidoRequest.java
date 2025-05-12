// src/main/java/sv/edu/udb/InvestigacionDwf/dto/request/PedidoRequest.java
package sv.edu.udb.InvestigacionDwf.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sv.edu.udb.InvestigacionDwf.model.enums.TipoPago;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequest {
    @NotNull(message = "El carrito es obligatorio")
    private Long idCarrito;

    @NotNull(message = "La forma de pago es obligatoria")
    private TipoPago tipoPago;

    private String cuponCodigo;//campo para el cupón

    private Long idDireccion;
}