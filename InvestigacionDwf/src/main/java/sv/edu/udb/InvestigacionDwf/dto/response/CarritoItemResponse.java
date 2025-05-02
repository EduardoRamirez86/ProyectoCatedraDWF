package sv.edu.udb.InvestigacionDwf.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// CarritoItemResponse.java
// CarritoItemResponse.java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarritoItemResponse {
    private Long idCarritoItem;
    private Long idCarrito;
    private Long idProducto;
    private Integer cantidad;
    private ProductoResponse producto; // Nuevo campo agregado
}
