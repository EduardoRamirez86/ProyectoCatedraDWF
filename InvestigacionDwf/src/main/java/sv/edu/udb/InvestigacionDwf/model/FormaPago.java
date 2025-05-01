// FormaPago.java
package sv.edu.udb.InvestigacionDwf.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "FormaPago")
public class FormaPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFormaPago;

    @Column(nullable = false)
    private String tipo;

    @OneToMany(mappedBy = "formaPago", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Pedido> pedidos;
}

