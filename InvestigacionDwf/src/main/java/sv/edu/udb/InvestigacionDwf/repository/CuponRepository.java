package sv.edu.udb.InvestigacionDwf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.udb.InvestigacionDwf.model.entity.Cupon;

import java.util.Optional;

public interface CuponRepository extends JpaRepository<Cupon, Long> {
    Optional<Cupon> findByCodigo(String codigo);
}


