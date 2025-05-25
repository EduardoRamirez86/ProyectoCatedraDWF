package sv.edu.udb.InvestigacionDwf.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import sv.edu.udb.InvestigacionDwf.dto.response.NotificacionResponse;
import sv.edu.udb.InvestigacionDwf.exception.ResourceNotFoundException;
import sv.edu.udb.InvestigacionDwf.model.entity.Notificacion;
import sv.edu.udb.InvestigacionDwf.model.entity.User;
import sv.edu.udb.InvestigacionDwf.model.enums.EstadoNotificacion;
import sv.edu.udb.InvestigacionDwf.repository.NotificacionRepository;
import sv.edu.udb.InvestigacionDwf.repository.UserRepository;
import sv.edu.udb.InvestigacionDwf.service.mapper.NotificacionMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class NotificacionServiceImplTest {

    @InjectMocks
    private NotificacionServiceImpl service;

    @Mock
    private NotificacionRepository notificacionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificacionMapper notificacionMapper;

    @Test
    void obtenerNotificacionesUsuario_shouldReturnList() {
        User user = new User();
        Notificacion notif = new Notificacion();
        NotificacionResponse resp = new NotificacionResponse();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificacionRepository.findByUser(user)).thenReturn(List.of(notif));
        when(notificacionMapper.toResponse(notif)).thenReturn(resp);

        List<NotificacionResponse> result = service.obtenerNotificacionesUsuario(1L);
        assertEquals(1, result.size());
        assertEquals(resp, result.get(0));
    }

    @Test
    void obtenerNotificacionesUsuario_shouldThrowIfNull() {
        assertThrows(IllegalArgumentException.class, () -> service.obtenerNotificacionesUsuario(null));
    }

    @Test
    void obtenerNotificacionesUsuario_shouldThrowIfUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.obtenerNotificacionesUsuario(1L));
    }

    @Test
    void marcarComoLeida_shouldUpdateEstado() {
        Notificacion notif = new Notificacion();
        notif.setEstado(EstadoNotificacion.ENVIADA);
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notif));
        when(notificacionRepository.save(notif)).thenReturn(notif);

        service.marcarComoLeida(1L);

        assertEquals(EstadoNotificacion.LEIDA, notif.getEstado());
        verify(notificacionRepository).save(notif);
    }

    @Test
    void marcarComoLeida_shouldThrowIfNull() {
        assertThrows(IllegalArgumentException.class, () -> service.marcarComoLeida(null));
    }

    @Test
    void marcarComoLeida_shouldThrowIfNotFound() {
        when(notificacionRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.marcarComoLeida(1L));
    }
}
