// NotificacionRequest.java (opcional, si se necesitan crear manualmente)
package sv.edu.udb.InvestigacionDwf.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificacionRequest {
    @NotNull(message = "El id de usuario es necesario")
    private Long userId;

    @NotNull(message = "El id del pedido se requiere")
    private Long pedidoId;

    @NotBlank(message = "El mensaje no puede estar vacio")
    private String mensaje;
}
