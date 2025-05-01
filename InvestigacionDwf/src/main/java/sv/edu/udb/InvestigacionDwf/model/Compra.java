package sv.edu.udb.InvestigacionDwf.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "compra")
public class Compra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCompra;

    // Fecha y hora de la compra
    @Column
    private LocalDateTime fechaCompra;

    // Cantidad de productos comprados
    @Column
    private Integer cantidad;

    // Total de la compra (se calcula como precio * cantidad)
    @Column
    private Double total;

    // Relación con el usuario que realiza la compra
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Relación con la prenda de ropa comprada
    @ManyToOne
    @JoinColumn(name = "ropa_id")
    private Ropa ropa;

    // Método para calcular el total
    public void calcularTotal() {
        if(ropa != null && cantidad != null && ropa.getPrecio() != null) {
            this.total = ropa.getPrecio() * cantidad;
        }
    }
}
