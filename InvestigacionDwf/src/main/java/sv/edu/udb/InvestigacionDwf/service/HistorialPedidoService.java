// src/main/java/sv/edu/udb/InvestigacionDwf/service/HistorialPedidoService.java
package sv.edu.udb.InvestigacionDwf.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import sv.edu.udb.InvestigacionDwf.dto.response.HistorialPedidoResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.HistorialPedido;

public interface HistorialPedidoService {
    PagedModel<HistorialPedidoResponse> findAll(
            Pageable pageable,
            PagedResourcesAssembler<HistorialPedido> pagedAssembler
    );
    HistorialPedidoResponse getById(Long id);
}

