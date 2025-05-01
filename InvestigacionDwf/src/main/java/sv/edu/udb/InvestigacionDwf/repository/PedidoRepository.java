package sv.edu.udb.InvestigacionDwf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.edu.udb.InvestigacionDwf.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
