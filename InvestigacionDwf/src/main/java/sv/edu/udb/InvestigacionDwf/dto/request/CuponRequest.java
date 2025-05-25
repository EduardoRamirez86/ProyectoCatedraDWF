// src/main/java/sv/edu/udb/InvestigacionDwf/dto/request/CuponRequest.java
package sv.edu.udb.InvestigacionDwf.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CuponRequest {
    @NotBlank(message = "Agregar una cadena de caracteres valida")
    private String codigo;
}