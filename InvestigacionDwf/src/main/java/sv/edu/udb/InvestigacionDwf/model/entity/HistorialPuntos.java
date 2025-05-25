package sv.edu.udb.InvestigacionDwf.model.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "HistorialPuntos")
public class HistorialPuntos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHistorialPuntos; // Nombre original

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user") // Nombre original
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido") // Nombre original
    private Pedido pedido;

    private LocalDateTime fecha;
    private Integer cantidadAnterior;
    private Integer cantidadNueva;
}