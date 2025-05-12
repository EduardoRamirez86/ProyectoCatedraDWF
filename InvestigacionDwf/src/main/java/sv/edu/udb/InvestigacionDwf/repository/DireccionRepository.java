// src/main/java/sv/edu/udb/InvestigacionDwf/repository/DireccionRepository.java
package sv.edu.udb.InvestigacionDwf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.udb.InvestigacionDwf.model.entity.Direccion;
import java.util.List;

public interface DireccionRepository extends JpaRepository<Direccion, Long> {
    List<Direccion> findByUser_IdUser(Long idUser);
}

