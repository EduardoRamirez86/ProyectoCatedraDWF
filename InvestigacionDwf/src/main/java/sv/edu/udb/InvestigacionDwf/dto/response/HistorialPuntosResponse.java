package sv.edu.udb.InvestigacionDwf.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class HistorialPuntosResponse {
    private Long idHistorialPuntos;
    private Long idUser;
    private Long idPedido;
    private LocalDateTime fecha;
    private Integer cantidadAnterior;
    private Integer cantidadNueva;
}

