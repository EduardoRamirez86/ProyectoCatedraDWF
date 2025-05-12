package sv.edu.udb.InvestigacionDwf.service.mapper;

import sv.edu.udb.InvestigacionDwf.model.entity.Notificacion;
import sv.edu.udb.InvestigacionDwf.dto.response.NotificacionResponse;

public class NotificacionMapper {

    public static NotificacionResponse toResponse(Notificacion notificacion) {
        NotificacionResponse response = new NotificacionResponse();
        response.setId(notificacion.getId());
        response.setMensaje(notificacion.getMensaje());
        response.setFechaEnvio(notificacion.getFechaEnvio());
        response.setEstado(notificacion.getEstado().name());
        response.setPedidoId(notificacion.getPedido().getIdPedido());
        return response;
    }
}

