package sv.edu.udb.InvestigacionDwf.controller;

import lombok.RequiredArgsConstructor;
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
    @ResponseStatus(HttpStatus.OK) // Aunque es un POST, el original devolvía 200 OK. Si la creación implica un nuevo recurso, HttpStatus.CREATED (201) sería más apropiado.
    public ResenaResponse crearResena(@RequestBody ResenaRequest request) {
        return resenaService.crearResena(request);
    }

    @GetMapping("/producto/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<ResenaResponse> obtenerResenas(@PathVariable Long id) {
        return resenaService.obtenerResenasPorProducto(id);
    }
}