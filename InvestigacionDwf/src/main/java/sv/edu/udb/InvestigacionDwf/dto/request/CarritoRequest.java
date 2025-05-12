package sv.edu.udb.InvestigacionDwf.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// CarritoRequest.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoRequest {
    private Long idUser;
    // no enviamos fechaCreacion (se setea en backend)
}
