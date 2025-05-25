package sv.edu.udb.InvestigacionDwf.dto.request;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class HistorialPuntosRequest {
    private Long idUser;
    private Long idPedido;
    private LocalDateTime fecha;
    private Integer cantidadAnterior;
    private Integer cantidadNueva;
}

