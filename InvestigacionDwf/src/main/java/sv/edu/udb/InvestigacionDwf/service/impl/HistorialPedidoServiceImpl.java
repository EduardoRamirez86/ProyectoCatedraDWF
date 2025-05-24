// src/main/java/sv/edu/udb/InvestigacionDwf/service/impl/HistorialPedidoServiceImpl.java
package sv.edu.udb.InvestigacionDwf.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.InvestigacionDwf.dto.response.HistorialPedidoResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.HistorialPedido;
import sv.edu.udb.InvestigacionDwf.repository.HistorialPedidoRepository;
import sv.edu.udb.InvestigacionDwf.service.assembler.HistorialPedidoAssembler;
import sv.edu.udb.InvestigacionDwf.service.HistorialPedidoService;

@Service
@RequiredArgsConstructor
public class HistorialPedidoServiceImpl implements HistorialPedidoService {

    private final HistorialPedidoRepository repo;
    private final HistorialPedidoAssembler assembler;

    @Override
    @Transactional(readOnly = true)
    public PagedModel<HistorialPedidoResponse> findAll(
            Pageable pageable,
            PagedResourcesAssembler<HistorialPedido> pagedAssembler
    ) {
        Page<HistorialPedido> page = repo.findAll(pageable);
        return pagedAssembler.toModel(page, assembler);
    }

    @Override
    @Transactional(readOnly = true)
    public HistorialPedidoResponse getById(Long id) {
        HistorialPedido e = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe HistorialPedido ID: " + id));
        return assembler.toModel(e);
    }
}


