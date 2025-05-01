// TipoProducto.java
package sv.edu.udb.InvestigacionDwf.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "TipoProducto")
public class TipoProducto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTipoProducto; // Ensure this property exists

    @Column(nullable = false)
    private String tipo;

    private String descripcion;

    @OneToMany(mappedBy = "tipoProducto", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Producto> productos;
}

