// NotificacionResponse.java
package sv.edu.udb.InvestigacionDwf.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificacionResponse {
    private Long id;
    private String mensaje;
    private LocalDateTime fechaEnvio;
    private String estado;
    private Long pedidoId;
}

