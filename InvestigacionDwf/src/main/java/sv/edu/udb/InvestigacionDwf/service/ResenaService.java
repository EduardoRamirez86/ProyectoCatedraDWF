package sv.edu.udb.InvestigacionDwf.service;

import sv.edu.udb.InvestigacionDwf.dto.request.ResenaRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.ResenaResponse;

import java.util.List;

public interface ResenaService {
    ResenaResponse crearResena(ResenaRequest request);
    List<ResenaResponse> obtenerResenasPorProducto(Long idProducto);
}

