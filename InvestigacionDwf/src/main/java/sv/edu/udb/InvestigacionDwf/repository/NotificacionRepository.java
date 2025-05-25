package sv.edu.udb.InvestigacionDwf.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.udb.InvestigacionDwf.model.entity.Notificacion;
import sv.edu.udb.InvestigacionDwf.model.entity.User;
import sv.edu.udb.InvestigacionDwf.model.enums.EstadoNotificacion;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByUserAndEstado(User user, EstadoNotificacion estado);
    List<Notificacion> findByUser(User user);
}

