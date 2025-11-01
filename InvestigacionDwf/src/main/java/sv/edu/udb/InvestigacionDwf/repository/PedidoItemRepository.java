package sv.edu.udb.InvestigacionDwf.repository; // Asegúrate que el paquete sea el correcto

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sv.edu.udb.InvestigacionDwf.model.entity.PedidoItem;
import sv.edu.udb.InvestigacionDwf.model.enums.EstadoPedido;

import java.util.List;

/**
 * Repositorio para la entidad PedidoItem.
 * Se encarga de las operaciones de base de datos relacionadas
 * con los ítems de un pedido.
 */
@Repository
public interface PedidoItemRepository extends JpaRepository<PedidoItem, Long> {

    // --- --- --- MÉTODO 1: PARA LISTAS COMPLETAS (SIN LÍMITE) --- --- ---
    /**
     * Busca TODOS los productos vendidos para un estado de pedido, sin aplicar límite desde la BD.
     * Útil para procesos internos o reportes que necesitan la lista completa.
     * La anotación @Param es opcional aquí si el nombre del parámetro 'estado' coincide.
     *
     * @param estado El estado del pedido por el cual filtrar.
     * @return Una lista COMPLETA de Object[], donde cada array contiene [Producto, CantidadVendida].
     */
    @Query(value = "SELECT pi.producto, SUM(pi.cantidad) as totalCantidad " +
            "FROM PedidoItem pi " +
            "JOIN pi.pedido p " +
            "WHERE p.estado = :estado " +
            "GROUP BY pi.producto " +
            "ORDER BY totalCantidad DESC")
    List<Object[]> findTopSellingProductsRawByEstado(@Param("estado") EstadoPedido estado);

    // --- --- --- MÉTODO 2: PARA LISTAS LIMITADAS (CON PAGINACIÓN) --- --- ---
    /**
     * Busca los productos más vendidos para un estado de pedido, APLICANDO UN LÍMITE a través de Pageable.
     * Es la versión que usa el dashboard para obtener solo los "top 5".
     *
     * @param estado El estado del pedido por el cual filtrar (ej. EstadoPedido.ENTREGADO).
     * @param pageable Un objeto Pageable que Spring Data usa para aplicar un LÍMITE a la consulta.
     * @return Una lista LIMITADA de Object[], donde cada array contiene [Producto, CantidadVendida].
     */
    @Query(value = "SELECT pi.producto, SUM(pi.cantidad) as totalCantidad " +
            "FROM PedidoItem pi " +
            "JOIN pi.pedido p " +
            "WHERE p.estado = :estado " +
            "GROUP BY pi.producto " +
            "ORDER BY totalCantidad DESC")
    List<Object[]> findTopSellingProductsRawByEstado(@Param("estado") EstadoPedido estado, Pageable pageable);

}

