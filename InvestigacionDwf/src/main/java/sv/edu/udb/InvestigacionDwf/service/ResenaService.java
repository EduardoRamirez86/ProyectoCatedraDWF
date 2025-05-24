// src/main/java/sv/edu/udb/InvestigacionDwf/service/ResenaService.java
package sv.edu.udb.InvestigacionDwf.service;

import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import sv.edu.udb.InvestigacionDwf.dto.request.ResenaRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.ResenaResponse;

import java.util.List;

public interface ResenaService {
    ResenaResponse crearResena(ResenaRequest request);
    List<ResenaResponse> obtenerResenasPorProducto(Long idProducto);
    PagedModel<ResenaResponse> findAll(Pageable pageable);
    ResenaResponse getById(Long idResena);

    PagedModel<ResenaResponse> obtenerResenasPorProductoPaginadas(Long idProducto, Pageable pageable);
}


