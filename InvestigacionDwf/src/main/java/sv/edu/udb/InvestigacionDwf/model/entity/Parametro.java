// 1. Entidad Parametro
package sv.edu.udb.InvestigacionDwf.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parametros")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parametro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_parametro")
    private Long idParametro;

    @Column(unique = true, nullable = false)
    private String clave;

    @Column(nullable = false)
    private String valor;

    @Column(nullable = false)
    private String descripcion;
}
