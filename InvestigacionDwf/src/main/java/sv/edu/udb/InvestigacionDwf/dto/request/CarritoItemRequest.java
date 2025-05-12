package sv.edu.udb.InvestigacionDwf.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// CarritoItemRequest.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoItemRequest {
    private Long idCarrito;
    private Long idProducto;
    private Integer cantidad;
}
