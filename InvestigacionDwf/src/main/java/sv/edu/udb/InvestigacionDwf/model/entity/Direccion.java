// Direccion.java
package sv.edu.udb.InvestigacionDwf.model.entity;

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
@Table(name = "Direccion")
public class Direccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDireccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    private String alias;           // “Casa”, “Trabajo”, etc.
    private String calle;           // Calle / dirección textual
    private String ciudad;
    private String departamento;
    private Double latitud;         // Coordenadas
    private Double longitud;
}


