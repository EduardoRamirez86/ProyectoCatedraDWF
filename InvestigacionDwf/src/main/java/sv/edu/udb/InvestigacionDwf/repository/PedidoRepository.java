package sv.edu.udb.InvestigacionDwf.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sv.edu.udb.InvestigacionDwf.model.entity.Pedido;
import sv.edu.udb.InvestigacionDwf.model.enums.EstadoPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    Page<Pedido> findByCarrito_User_IdUser(Long idUser, Pageable pageable);
    // --- NUEVOS MÉTODOS PARA EL DASHBOARD ---

    // Suma de ganancias estimadas para pedidos con un estado específico
    @Query("SELECT SUM(p.gananciaEstimada) FROM Pedido p WHERE p.estado = :estado")
    Optional<BigDecimal> sumGananciaEstimadaByEstado(@Param("estado") EstadoPedido estado);

    // Suma de ganancias estimadas en un rango de fechas para pedidos con un estado específico
    @Query("SELECT SUM(p.gananciaEstimada) FROM Pedido p WHERE p.fechaInicio BETWEEN :fechaInicio AND :fechaFin AND p.estado = :estado")
    Optional<BigDecimal> sumGananciaEstimadaByFechaBetweenAndEstado(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            @Param("estado") EstadoPedido estado);

    // Si también quieres obtener la ganancia total sin importar el estado
    @Query("SELECT SUM(p.gananciaEstimada) FROM Pedido p")
    Optional<BigDecimal> sumAllGananciaEstimada();

    /**
     * Busca todos los pedidos y los ordena según una prioridad de estado personalizada,
     * y luego por fecha de inicio descendente como criterio de desempate.
     * La cláusula CASE asigna un número de prioridad a cada estado.
     * Prioridad 1: PAGADO (más importante)
     * Prioridad 2: ENVIADO
     * Prioridad 3: EN_PROCESO
     * Prioridad 4: PENDIENTE
     * Prioridad 5: ENTREGADO
     * Prioridad 6: CANCELADO (menos importante)
     *
     * @param pageable Objeto de paginación para limitar los resultados.
     * @return una Página (Page) de Pedidos ordenados por prioridad.
     */
    @Query("SELECT p FROM Pedido p WHERE p.estado != sv.edu.udb.InvestigacionDwf.model.enums.EstadoPedido.CARRITO ORDER BY " +
            "CASE p.estado " +
            "WHEN sv.edu.udb.InvestigacionDwf.model.enums.EstadoPedido.PAGADO THEN 1 " +
            "WHEN sv.edu.udb.InvestigacionDwf.model.enums.EstadoPedido.ENVIADO THEN 2 " +
            "WHEN sv.edu.udb.InvestigacionDwf.model.enums.EstadoPedido.EN_PROCESO THEN 3 " +
            "WHEN sv.edu.udb.InvestigacionDwf.model.enums.EstadoPedido.PENDIENTE THEN 4 " +
            "WHEN sv.edu.udb.InvestigacionDwf.model.enums.EstadoPedido.ENTREGADO THEN 5 " +
            "WHEN sv.edu.udb.InvestigacionDwf.model.enums.EstadoPedido.CANCELADO THEN 6 " +
            "ELSE 7 " +
            "END ASC, " +
            "p.fechaInicio DESC")
    Page<Pedido> findAllByPriority(Pageable pageable);
}
