package sv.edu.udb.InvestigacionDwf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sv.edu.udb.InvestigacionDwf.model.entity.Carrito;
import sv.edu.udb.InvestigacionDwf.model.entity.CarritoItem;

import java.util.List;

public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {
    List<CarritoItem> findByCarrito_IdCarrito(Long idCarrito);

    @Modifying
    @Query("DELETE FROM CarritoItem ci WHERE ci.carrito = :carrito")
    void deleteByCarrito(@Param("carrito") Carrito carrito);
}
