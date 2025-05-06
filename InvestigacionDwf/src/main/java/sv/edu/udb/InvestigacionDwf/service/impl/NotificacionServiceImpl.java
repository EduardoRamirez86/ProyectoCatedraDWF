package sv.edu.udb.InvestigacionDwf.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sv.edu.udb.InvestigacionDwf.model.enums.EstadoNotificacion;
import sv.edu.udb.InvestigacionDwf.model.entity.Notificacion;
import sv.edu.udb.InvestigacionDwf.repository.NotificacionRepository;
import sv.edu.udb.InvestigacionDwf.repository.UserRepository;
import sv.edu.udb.InvestigacionDwf.dto.response.NotificacionResponse;
import sv.edu.udb.InvestigacionDwf.service.NotificacionService;
import sv.edu.udb.InvestigacionDwf.service.mapper.NotificacionMapper;

@Service
public class NotificacionServiceImpl implements NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<NotificacionResponse> obtenerNotificacionesUsuario(Long idUsuario) {
        var user = userRepository.findById(idUsuario).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return notificacionRepository.findByUser(user).stream()
                .map(NotificacionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void marcarComoLeida(Long idNotificacion) {
        Notificacion notif = notificacionRepository.findById(idNotificacion)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        notif.setEstado(EstadoNotificacion.LEIDA);
        notificacionRepository.save(notif);
    }
}

