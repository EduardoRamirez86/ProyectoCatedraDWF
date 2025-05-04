// Direccion.java
package sv.edu.udb.InvestigacionDwf.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Direccion")
public class Direccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDireccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    private User user;

    private String pais;
    private String departamento;
    private String ciudad;
    private String calle;
    private String codigoPostal;
    private String numeroDomicilio;
}

