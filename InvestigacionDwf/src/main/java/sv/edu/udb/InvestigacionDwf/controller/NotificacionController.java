package sv.edu.udb.InvestigacionDwf.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import sv.edu.udb.InvestigacionDwf.dto.response.NotificacionResponse;
import sv.edu.udb.InvestigacionDwf.service.NotificacionService;

@RestController
@RequestMapping("/auth/notificacion")
@CrossOrigin(origins = "http://localhost:3000")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @GetMapping("/usuario/{id}")
    public List<NotificacionResponse> obtenerNotificaciones(@PathVariable Long id) {
        return notificacionService.obtenerNotificacionesUsuario(id);
    }

    @PutMapping("/leer/{id}")
    public void marcarComoLeida(@PathVariable Long id) {
        notificacionService.marcarComoLeida(id);
    }
}
