package sv.edu.udb.InvestigacionDwf.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import sv.edu.udb.InvestigacionDwf.model.enums.RatingEnum;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResenaRequest {
    @NotNull(message = "Se necesita el id del usuario")
    private Long idUser;

    @NotNull(message = "Se necesita el id del producto")
    private Long idProducto;

    private String comentario;

    private RatingEnum rating;
}
