// NotificacionRequest.java (opcional, si se necesitan crear manualmente)
package sv.edu.udb.InvestigacionDwf.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificacionRequest {
    @NotNull
    private Long userId;

    @NotNull
    private Long pedidoId;

    @NotBlank
    private String mensaje;
}
