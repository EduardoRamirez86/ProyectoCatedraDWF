package sv.edu.udb.InvestigacionDwf.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TipoProductoRequest {
    @NotBlank(message = "Agregar que tipo es")
    private String tipo;

    private String descripcion;
}

