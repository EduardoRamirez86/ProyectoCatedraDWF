package sv.edu.udb.InvestigacionDwf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.udb.InvestigacionDwf.model.CarritoItem;

public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {
}
