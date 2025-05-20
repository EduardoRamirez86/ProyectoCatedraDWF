package sv.edu.udb.InvestigacionDwf.dto.response;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ProductoResponse extends RepresentationModel<ProductoResponse> {
    private Long idProducto;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private BigDecimal costo;
    private Integer cantidad;
    private String imagen;
    private Integer cantidadPuntos;
    private Long idTipoProducto;
    private String nombreTipo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}

