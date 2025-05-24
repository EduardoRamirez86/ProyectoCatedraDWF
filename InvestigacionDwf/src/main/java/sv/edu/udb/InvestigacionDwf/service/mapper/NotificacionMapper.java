package sv.edu.udb.InvestigacionDwf.service.mapper;

import org.springframework.stereotype.Component; // ¡Importa esta anotación!
import sv.edu.udb.InvestigacionDwf.model.entity.Notificacion;
import sv.edu.udb.InvestigacionDwf.dto.response.NotificacionResponse;

import java.util.Objects;

@Component // ¡Añade esta anotación para que Spring la detecte como un bean!
public class NotificacionMapper {

    /**
     * Convierte una entidad Notificacion a un DTO de respuesta (NotificacionResponse).
     * Se asegura de que la entidad y sus relaciones clave (Pedido) no sean nulas
     * antes de intentar acceder a sus propiedades para evitar NullPointerExceptions.
     * Utiliza el patrón Builder de Lombok para una creación de objeto más legible.
     *
     * @param notificacion La entidad Notificacion a convertir.
     * @return Un DTO NotificacionResponse con los datos de la notificación.
     * @throws IllegalArgumentException si la entidad Notificacion o su relación esencial (Pedido) es nula.
     */
    // Quita 'static' aquí
    public NotificacionResponse toResponse(Notificacion notificacion) {
        // Valida que la entidad Notificacion no sea nula.
        if (Objects.isNull(notificacion)) {
            throw new IllegalArgumentException("La entidad Notificacion no puede ser nula para mapear a DTO.");
        }
        // Valida que la relación 'Pedido' no sea nula, ya que se accede a su ID.
        if (Objects.isNull(notificacion.getPedido())) {
            throw new IllegalArgumentException("La notificación debe tener un Pedido asociado para mapear a DTO.");
        }

        return NotificacionResponse.builder()
                .id(notificacion.getId())
                .mensaje(notificacion.getMensaje())
                .fechaEnvio(notificacion.getFechaEnvio())
                .estado(notificacion.getEstado().name())
                .pedidoId(notificacion.getPedido().getIdPedido())
                .build();
    }
}


