package sv.edu.udb.InvestigacionDwf.model.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sv.edu.udb.InvestigacionDwf.model.enums.EstadoPedido;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "HistorialPedido")
public class HistorialPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHistorialPedido; // Nombre original

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido") // Nombre original
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user") // Nombre original
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPedido estado; // Usando enum



    private LocalDateTime fecha;
    private String descripcion;
}
