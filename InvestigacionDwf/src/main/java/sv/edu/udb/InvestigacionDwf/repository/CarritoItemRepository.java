package sv.edu.udb.InvestigacionDwf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sv.edu.udb.InvestigacionDwf.model.entity.Carrito;
import sv.edu.udb.InvestigacionDwf.model.entity.CarritoItem;
import sv.edu.udb.InvestigacionDwf.model.enums.EstadoPedido;
import sv.edu.udb.InvestigacionDwf.model.entity.Producto; // Asegúrate de importar Producto

import java.util.List;

public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {
    List<CarritoItem> findByCarrito_IdCarrito(Long idCarrito);

    @Modifying
    @Query("DELETE FROM CarritoItem ci WHERE ci.carrito = :carrito")
    void deleteByCarrito(@Param("carrito") Carrito carrito);

    // --- MÉTODO CORREGIDO PARA PRODUCTOS MÁS VENDIDOS ---
    // La consulta debe empezar desde Pedido, que es quien tiene la relación con Carrito,
    // y luego unir con CarritoItem usando la relación CarritoItem.carrito
    @Query("SELECT ci.producto, SUM(ci.cantidad) as totalCantidad " +
            "FROM Pedido p " +            // Empezamos desde Pedido
            "JOIN p.carrito c " +         // Unimos Pedido con su Carrito
            "JOIN CarritoItem ci ON ci.carrito = c " + // Unimos CarritoItem con Carrito explícitamente
            "WHERE p.estado = :estado " + // Filtramos por el estado del Pedido
            "GROUP BY ci.producto " +
            "ORDER BY totalCantidad DESC")
    List<Object[]> findTopSellingProductsRawByEstado(@Param("estado") EstadoPedido estado);
}
