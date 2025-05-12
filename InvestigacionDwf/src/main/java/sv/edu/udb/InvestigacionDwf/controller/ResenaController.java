package sv.edu.udb.InvestigacionDwf.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ResenaResponse> crearResena(@RequestBody ResenaRequest request) {
        return ResponseEntity.ok(resenaService.crearResena(request));
    }

    @GetMapping("/producto/{id}")
    public ResponseEntity<List<ResenaResponse>> obtenerResenas(@PathVariable Long id) {
        return ResponseEntity.ok(resenaService.obtenerResenasPorProducto(id));
    }
}
