// Pedido.java
package sv.edu.udb.InvestigacionDwf.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Pedido")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFinal;
    private BigDecimal total;
    private Integer puntosTotales;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_forma_pago", nullable = false)
    private FormaPago formaPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrito")
    private Carrito carrito;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<HistorialPedido> historialPedidos;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<HistorialPuntos> historialPuntos;
}

