// src/main/java/sv/edu/udb/InvestigacionDwf/repository/TipoProductoRepository.java
package sv.edu.udb.InvestigacionDwf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv.edu.udb.InvestigacionDwf.model.entity.TipoProducto;

import java.util.Optional;

@Repository
public interface TipoProductoRepository extends JpaRepository<TipoProducto, Long> {
    Optional<TipoProducto> findByTipo(String tipo);
}


