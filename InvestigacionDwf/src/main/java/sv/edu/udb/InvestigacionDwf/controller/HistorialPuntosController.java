// src/main/java/sv/edu/udb/InvestigacionDwf/controller/HistorialPuntosController.java
package sv.edu.udb.InvestigacionDwf.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.InvestigacionDwf.model.entity.HistorialPuntos;
import sv.edu.udb.InvestigacionDwf.dto.response.HistorialPuntosResponse;
import sv.edu.udb.InvestigacionDwf.service.HistorialPuntosService;
import sv.edu.udb.InvestigacionDwf.service.assembler.HistorialPuntosAssembler;

@RestController
@RequestMapping("/auth/historial-puntos")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class HistorialPuntosController {

    private final HistorialPuntosService       service;
    private final HistorialPuntosAssembler     assembler;

    @GetMapping
    public PagedModel<HistorialPuntosResponse> getAll(
            Pageable pageable,
            PagedResourcesAssembler<HistorialPuntos> pagedAssembler
    ) {
        var page = service.findAllEntities(pageable);
        return pagedAssembler.toModel(page, assembler);
    }

    @GetMapping("/{id}")
    public HistorialPuntosResponse getById(@PathVariable Long id) {
        var entity = service.getByIdEntity(id);
        return assembler.toModel(entity);
    }

    @GetMapping("/usuario/{idUser}")
    public PagedModel<HistorialPuntosResponse> getByUser(
            @PathVariable Long idUser,
            Pageable pageable,
            PagedResourcesAssembler<HistorialPuntos> pagedAssembler
    ) {
        var page = service.findByUserIdOrderByPedidoDesc(idUser, pageable);
        return pagedAssembler.toModel(page, assembler);
    }
}


