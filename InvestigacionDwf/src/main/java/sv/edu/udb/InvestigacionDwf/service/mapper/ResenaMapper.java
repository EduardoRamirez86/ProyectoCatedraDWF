package sv.edu.udb.InvestigacionDwf.service.mapper;

import sv.edu.udb.InvestigacionDwf.dto.response.ResenaResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.Resena;

public class ResenaMapper {
    public static ResenaResponse toDto(Resena resena) {
        ResenaResponse dto = new ResenaResponse();
        dto.setIdResena(resena.getIdResena());
        dto.setUsername(resena.getUser().getUsername());
        dto.setProductoNombre(resena.getProducto().getNombre());
        dto.setComentario(resena.getComentario());
        dto.setFecha(resena.getFecha());
        dto.setRating(resena.getRating());
        return dto;
    }
}

