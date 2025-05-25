package sv.edu.udb.InvestigacionDwf.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "registros_ganancias")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroGanancia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRegistro;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", unique = true, nullable = false)
    private Pedido pedido;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "monto_ganancia", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoGanancia;
}
