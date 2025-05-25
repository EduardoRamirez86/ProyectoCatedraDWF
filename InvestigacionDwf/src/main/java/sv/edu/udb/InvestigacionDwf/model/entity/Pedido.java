// src/main/java/sv/edu/udb/InvestigacionDwf/model/entity/Pedido.java
package sv.edu.udb.InvestigacionDwf.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sv.edu.udb.InvestigacionDwf.model.enums.EstadoPedido;
import sv.edu.udb.InvestigacionDwf.model.enums.TipoPago;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedido;

    @Column(nullable = false)
    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFinal;

    @Column(precision = 10, scale = 2, nullable = false)
    @NotNull(message = "El total es obligatorio")
    private BigDecimal total;

    // --- NUEVOS CAMPOS ---
    @Column(precision = 10, scale = 2) // Opcional, pero útil para tener el subtotal registrado
    private BigDecimal subtotal;

    @Column(name = "descuento_aplicado", precision = 10, scale = 2)
    private BigDecimal descuentoAplicado = BigDecimal.ZERO; // Almacena el descuento aplicado

    @Column(name = "ganancia_estimada", precision = 10, scale = 2)
    private BigDecimal gananciaEstimada; // Ganancia neta para este pedido

    @Column(nullable = false)
    private Integer puntosTotales = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPedido estado = EstadoPedido.CARRITO;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pago", nullable = false, length = 30)
    private TipoPago tipoPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id")
    private Carrito carrito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_direccion")
    private Direccion direccion;

    // --- NUEVO: List of PedidoItems ---
    /**
     * Lista de los ítems de este pedido.
     * `cascade = CascadeType.ALL` asegura que los PedidoItem se persistan, actualicen y eliminen con el Pedido.
     * `orphanRemoval = true` asegura que si un PedidoItem se elimina de la lista, se elimine de la DB.
     */
    @Builder.Default
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Evitar referencias circulares en serialización JSON
    private List<PedidoItem> pedidoItems = new ArrayList<>();

    /** Garantiza que el builder inicialice la lista */
    @Builder.Default
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<HistorialPedido> historialPedidos = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<HistorialPuntos> historialPuntos = new ArrayList<>();

    /**
     * Añade un nuevo paso al historial de estados
     */
    public void actualizarEstado(EstadoPedido nuevoEstado, User usuario) {
        var historial = HistorialPedido.builder()
                .pedido(this)
                .estado(nuevoEstado)
                .fecha(LocalDateTime.now())
                .user(usuario)
                .build();
        this.historialPedidos.add(historial);
        this.estado = nuevoEstado;
    }
}