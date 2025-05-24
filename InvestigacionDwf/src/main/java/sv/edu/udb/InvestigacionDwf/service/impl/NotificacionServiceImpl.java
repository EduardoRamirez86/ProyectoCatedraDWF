package sv.edu.udb.InvestigacionDwf.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importa Transactional

import sv.edu.udb.InvestigacionDwf.exception.ResourceNotFoundException; // Importa ResourceNotFoundException
import sv.edu.udb.InvestigacionDwf.model.enums.EstadoNotificacion;
import sv.edu.udb.InvestigacionDwf.model.entity.Notificacion;
import sv.edu.udb.InvestigacionDwf.model.entity.User; // Importa User
import sv.edu.udb.InvestigacionDwf.repository.NotificacionRepository;
import sv.edu.udb.InvestigacionDwf.repository.UserRepository;
import sv.edu.udb.InvestigacionDwf.dto.response.NotificacionResponse;
import sv.edu.udb.InvestigacionDwf.service.NotificacionService;
import sv.edu.udb.InvestigacionDwf.service.mapper.NotificacionMapper;

import java.util.List;
import java.util.Objects; // Importa Objects para las validaciones de nulidad
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Inyecta las dependencias a través del constructor (eliminando @Autowired en campos)
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UserRepository userRepository;
    private final NotificacionMapper notificacionMapper; // Inyecta el mapper directamente

    @Override
    @Transactional(readOnly = true) // Método de solo lectura
    public List<NotificacionResponse> obtenerNotificacionesUsuario(Long idUsuario) {
        // Valida que el ID de usuario no sea nulo.
        if (Objects.isNull(idUsuario)) {
            throw new IllegalArgumentException("El ID de usuario no puede ser nulo para obtener notificaciones.");
        }

        // Busca el usuario por ID; si no se encuentra, lanza una ResourceNotFoundException.
        // Se utiliza la excepción semántica correcta en lugar de RuntimeException.
        User user = userRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + idUsuario));

        // Obtiene las notificaciones asociadas al usuario y las mapea a DTOs de respuesta.
        return notificacionRepository.findByUser(user).stream()
                .map(notificacionMapper::toResponse) // Usa la instancia inyectada del mapper
                .collect(Collectors.toList());
    }

    @Override
    @Transactional // Marca el método como transaccional para asegurar la actualización.
    public void marcarComoLeida(Long idNotificacion) {
        // Valida que el ID de la notificación no sea nulo.
        if (Objects.isNull(idNotificacion)) {
            throw new IllegalArgumentException("El ID de la notificación no puede ser nulo para marcar como leída.");
        }

        // Busca la notificación por ID; si no se encuentra, lanza una ResourceNotFoundException.
        Notificacion notif = notificacionRepository.findById(idNotificacion)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada con ID: " + idNotificacion));

        // Cambia el estado de la notificación a LEIDA.
        notif.setEstado(EstadoNotificacion.LEIDA);
        // Guarda la notificación actualizada en la base de datos.
        notificacionRepository.save(notif);
    }
}

