package sv.edu.udb.InvestigacionDwf.dto.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@Builder(toBuilder = true)
@FieldNameConstants
public class ProductoResponse {

    private Long idProducto;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private BigDecimal costo;
    private Integer cantidad;
    private String imagen;
    private Integer cantidadPuntos;

    // Relaciones como IDs
    private Long idTipoProducto;

    private String nombreTipo;

    @Override
    public String toString() {
        return "ProductoResponse{" +
                "idProducto=" + idProducto +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", precio=" + precio +
                ", costo=" + costo +
                ", cantidad=" + cantidad +
                ", imagen='" + imagen + '\'' +
                ", cantidadPuntos=" + cantidadPuntos +
                ", idTipoProducto=" + idTipoProducto +
                '}';
    }
}

