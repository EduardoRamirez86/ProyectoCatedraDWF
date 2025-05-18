package sv.edu.udb.InvestigacionDwf.service.mapper;

import sv.edu.udb.InvestigacionDwf.dto.response.ResenaResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.Resena;

public class ResenaMapper {
    public static ResenaResponse toDto(Resena resena) {
        return ResenaResponse.builder()
                .idResena(resena.getIdResena())
                .username(resena.getUser().getUsername())
                .productoNombre(resena.getProducto().getNombre())
                .comentario(resena.getComentario())
                .fecha(resena.getFecha())
                .rating(resena.getRating())
                .build();
    }
}


