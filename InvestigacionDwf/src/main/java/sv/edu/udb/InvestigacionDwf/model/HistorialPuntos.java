// HistorialPuntos.java
package sv.edu.udb.InvestigacionDwf.model;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "HistorialPuntos")
public class HistorialPuntos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idHistorialPuntos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    private LocalDateTime fecha;
    private Integer cantidadAnterior;
    private Integer cantidadNueva;
}
