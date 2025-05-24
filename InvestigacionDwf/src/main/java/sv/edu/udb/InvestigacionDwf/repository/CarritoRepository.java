package sv.edu.udb.InvestigacionDwf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv.edu.udb.InvestigacionDwf.model.entity.Carrito;

import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    Optional<Carrito> findByUser_IdUserAndPedidosIsEmpty(Long idUser);

    Optional<Carrito> findByUserIdUser(Long idUser);

}
