package sv.edu.udb.InvestigacionDwf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sv.edu.udb.InvestigacionDwf.model.entity.PedidoItem;
import sv.edu.udb.InvestigacionDwf.model.entity.Producto;
import sv.edu.udb.InvestigacionDwf.model.enums.EstadoPedido;
import org.springframework.data.domain.Pageable; // Importa Pageable

import java.util.List;

@Repository
public interface PedidoItemRepository extends JpaRepository<PedidoItem, Long> {

    // Mantén esta consulta para cuando no se necesite un límite específico y se quiera procesar en el servicio
    @Query(value = "SELECT pi.producto, SUM(pi.cantidad) as totalCantidad " +
            "FROM PedidoItem pi " +
            "JOIN pi.pedido p " +
            "WHERE p.estado = :estado " +
            "GROUP BY pi.producto " +
            "ORDER BY totalCantidad DESC")
    List<Object[]> findTopSellingProductsRawByEstado(@Param("estado") EstadoPedido estado);

    // Esta es la forma CORRECTA y flexible de aplicar el límite y paginación
    @Query(value = "SELECT pi.producto, SUM(pi.cantidad) as totalCantidad " +
            "FROM PedidoItem pi " +
            "JOIN pi.pedido p " +
            "WHERE p.estado = :estado " +
            "GROUP BY pi.producto " +
            "ORDER BY totalCantidad DESC")
    List<Object[]> findTopSellingProductsRawByEstado(@Param("estado") EstadoPedido estado, Pageable pageable);

    // Si tenías este método en CarritoItemRepository, y ahora usas PedidoItemRepository
    // te sugiero mantener la lógica de "productos más vendidos" relacionada con PedidoItem
    // ya que CarritoItem es más transitorio y PedidoItem es el registro final de la venta.
    // Si necesitas también los más vendidos de carritos (no convertidos a pedido), sería otra consulta.
}