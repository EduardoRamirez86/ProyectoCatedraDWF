package sv.edu.udb.InvestigacionDwf.service.mapper;

import sv.edu.udb.InvestigacionDwf.model.entity.Notificacion;
import sv.edu.udb.InvestigacionDwf.dto.response.NotificacionResponse;

public class NotificacionMapper {

    public static NotificacionResponse toResponse(Notificacion notificacion) {
        return NotificacionResponse.builder()
                .id(notificacion.getId())
                .mensaje(notificacion.getMensaje())
                .fechaEnvio(notificacion.getFechaEnvio())
                .estado(notificacion.getEstado().name())
                .pedidoId(notificacion.getPedido().getIdPedido())
                .build();
    }
}


