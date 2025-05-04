package sv.edu.udb.InvestigacionDwf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv.edu.udb.InvestigacionDwf.model.entity.Pedido;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByCarrito_User_IdUser(Long idUser);
}
