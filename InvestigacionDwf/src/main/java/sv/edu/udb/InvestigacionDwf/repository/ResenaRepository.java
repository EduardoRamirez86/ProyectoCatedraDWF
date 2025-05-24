package sv.edu.udb.InvestigacionDwf.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.udb.InvestigacionDwf.model.entity.Resena;

import java.util.List;

public interface ResenaRepository extends JpaRepository<Resena, Long> {
    List<Resena> findByProducto_IdProducto(Long idProducto);

    Page<Resena> findByProducto_IdProducto(Long idProducto, Pageable pageable);
}

