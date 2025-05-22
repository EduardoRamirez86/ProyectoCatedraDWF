// src/main/java/sv/edu/udb/InvestigacionDwf/dto/request/PedidoRequest.java
package sv.edu.udb.InvestigacionDwf.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sv.edu.udb.InvestigacionDwf.model.enums.TipoPago;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PedidoRequest {
    @NotNull(message = "El carrito es obligatorio")
    private Long idCarrito;

    @NotNull(message = "La forma de pago es obligatoria")
    private TipoPago tipoPago;

    private String cuponCodigo;

    private Long idDireccion;
}