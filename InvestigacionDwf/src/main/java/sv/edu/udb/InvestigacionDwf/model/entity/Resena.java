package sv.edu.udb.InvestigacionDwf.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sv.edu.udb.InvestigacionDwf.model.enums.RatingEnum;

import java.time.LocalDateTime;

@Entity
@Table(name = "Resena")
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idResena;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    private String comentario;

    private LocalDateTime fecha = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private RatingEnum rating;
}
