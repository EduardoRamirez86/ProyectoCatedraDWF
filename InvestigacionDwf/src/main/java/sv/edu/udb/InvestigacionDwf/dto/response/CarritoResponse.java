package sv.edu.udb.InvestigacionDwf.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

// CarritoResponse.java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarritoResponse {
    private Long idCarrito;
    private Long idUser;
    private LocalDateTime fechaCreacion;
    private List<CarritoItemResponse> items;
}
