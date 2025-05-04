package sv.edu.udb.InvestigacionDwf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.udb.InvestigacionDwf.model.entity.Notificacion;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
}
