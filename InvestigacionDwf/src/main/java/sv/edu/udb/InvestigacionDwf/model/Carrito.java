// Carrito.java
package sv.edu.udb.InvestigacionDwf.model;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Carrito")
public class Carrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCarrito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    private User user;

    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<CarritoItem> items;

    @OneToMany(mappedBy = "carrito", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Pedido> pedidos;
}

