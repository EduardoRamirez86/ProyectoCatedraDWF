// Estado.java
package sv.edu.udb.InvestigacionDwf.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Estado")
public class Estado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEstado;

    @Column(nullable = false)
    private String tipo;

    @OneToMany(mappedBy = "estado", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<HistorialPedido> historialPedidos;
}

