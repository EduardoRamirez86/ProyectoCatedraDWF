package sv.edu.udb.InvestigacionDwf.service;

import java.util.List;
import sv.edu.udb.InvestigacionDwf.dto.response.NotificacionResponse;

public interface NotificacionService {
    List<NotificacionResponse> obtenerNotificacionesUsuario(Long idUsuario);
    void marcarComoLeida(Long idNotificacion);
}

