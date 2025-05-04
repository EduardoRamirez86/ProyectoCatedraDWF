package sv.edu.udb.InvestigacionDwf.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "ropa")
public class Ropa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRopa;

    @Column(length = 50)
    private String nombre;

    // Atributo para el precio de la prenda
    @Column
    private Double precio;
}
