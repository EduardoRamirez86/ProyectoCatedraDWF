// EstadoMensaje.java
package sv.edu.udb.InvestigacionDwf.model.entity;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "EstadoMensaje")
public class EstadoMensaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEstadoMensaje;

    @Column(nullable = false)
    private String estado;

    @OneToMany(mappedBy = "estadoMensaje", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Notificacion> notificaciones;
}
