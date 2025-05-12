package sv.edu.udb.InvestigacionDwf.dto.response;

import lombok.Data;
import sv.edu.udb.InvestigacionDwf.model.enums.RatingEnum;

import java.time.LocalDateTime;

@Data
public class ResenaResponse {
    private Long idResena;
    private String username;
    private String productoNombre;
    private String comentario;
    private LocalDateTime fecha;
    private RatingEnum rating;
}
