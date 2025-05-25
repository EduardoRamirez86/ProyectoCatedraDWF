package sv.edu.udb.InvestigacionDwf.dto.request;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductoRequest {

    @NotNull(message = "El nombre es requerido")
    private String nombre;

    @NotNull(message = "La descripción es requerida")
    private String descripcion;

    @NotNull(message = "El precio es requerido")
    private BigDecimal precio;

    @NotNull(message = "El costo es requerido")
    private BigDecimal costo;

    @NotNull(message = "La cantidad es requerida")
    private Integer cantidad;

    private String imagen;

    @NotNull(message = "La cantidad de puntos es requerida")
    private Integer cantidadPuntos;

    @NotNull(message = "El ID del tipo de producto es requerido")
    private Long idTipoProducto;
}
