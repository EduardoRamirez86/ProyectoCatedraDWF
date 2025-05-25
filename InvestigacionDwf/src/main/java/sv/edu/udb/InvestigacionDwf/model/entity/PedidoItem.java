// src/main/java/sv/edu/udb/InvestigacionDwf/model/entity/PedidoItem.java
package sv.edu.udb.InvestigacionDwf.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "pedido_items") // Renamed from "detalle_pedido" to "pedido_items" for consistency
public class PedidoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido; // Link to the Pedido this item belongs to

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto; // Link to the Product that was bought

    @Column(nullable = false)
    private Integer cantidad; // Quantity of this product in the order

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal precioUnitario; // Price of the product at the time of purchase (important for historical accuracy)
}
