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
}
