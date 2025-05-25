// src/main/java/sv/edu/udb/InvestigacionDwf/controller/ResenaController.java
package sv.edu.udb.InvestigacionDwf.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.InvestigacionDwf.dto.request.ResenaRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.ResenaResponse;
import sv.edu.udb.InvestigacionDwf.service.ResenaService;

import java.util.List;

@RestController
@RequestMapping("/auth/resenas")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ResenaController {

    private final ResenaService resenaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResenaResponse crearResena(@RequestBody ResenaRequest request) {
        return resenaService.crearResena(request);
    }

    @GetMapping("/producto/{id}")
    public List<ResenaResponse> obtenerResenas(@PathVariable Long id) {
        return resenaService.obtenerResenasPorProducto(id);
    }

    /** Nuevo endpoint paginado HAL **/
    @GetMapping
    public PagedModel<ResenaResponse> getAll(Pageable pageable) {
        return resenaService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResenaResponse getById(@PathVariable Long id) {
        return resenaService.getById(id);
    }

    @GetMapping("/producto/{id}/paginated") // <-- Nuevo endpoint
    public PagedModel<ResenaResponse> getResenasByProductIdPaginated(
            @PathVariable Long id,
            Pageable pageable) { // Spring Boot inyecta Pageable automáticamente
        return resenaService.obtenerResenasPorProductoPaginadas(id, pageable);
    }
}

