package sv.edu.udb.InvestigacionDwf.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.udb.InvestigacionDwf.model.entity.HistorialPuntos;

public interface HistorialPuntosRepository extends JpaRepository<HistorialPuntos, Long> {
    Page<HistorialPuntos> findByUser_IdUserOrderByPedido_IdPedidoDesc(Long idUser, Pageable pageable);
}
