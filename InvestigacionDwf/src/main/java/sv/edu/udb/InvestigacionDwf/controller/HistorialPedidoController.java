// src/main/java/sv/edu/udb/InvestigacionDwf/controller/HistorialPedidoController.java
package sv.edu.udb.InvestigacionDwf.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.InvestigacionDwf.dto.response.HistorialPedidoResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.HistorialPedido;
import sv.edu.udb.InvestigacionDwf.service.HistorialPedidoService;

@RestController
@RequestMapping("/auth/historial-pedidos")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class HistorialPedidoController {

    private final HistorialPedidoService service;

    @GetMapping
    public PagedModel<HistorialPedidoResponse> getAll(
            Pageable pageable,
            PagedResourcesAssembler<HistorialPedido> pagedAssembler
    ) {
        // delegamos al servicio pasándole el assembler
        return service.findAll(pageable, pagedAssembler);
    }

    @GetMapping("/{id}")
    public HistorialPedidoResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }
}

