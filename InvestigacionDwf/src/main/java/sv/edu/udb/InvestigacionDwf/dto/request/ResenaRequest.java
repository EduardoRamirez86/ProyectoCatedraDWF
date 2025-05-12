package sv.edu.udb.InvestigacionDwf.dto.request;

import lombok.Data;
import sv.edu.udb.InvestigacionDwf.model.enums.RatingEnum;

@Data
public class ResenaRequest {
    private Long idUser;
    private Long idProducto;
    private String comentario;
    private RatingEnum rating;
}
