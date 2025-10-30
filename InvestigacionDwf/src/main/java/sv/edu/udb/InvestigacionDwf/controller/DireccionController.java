package sv.edu.udb.InvestigacionDwf.controller;import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity; // ¡IMPORTANTE!
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.InvestigacionDwf.dto.request.DireccionRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.DireccionResponse;
import sv.edu.udb.InvestigacionDwf.service.DireccionService;

import java.util.List;

@RestController
@RequestMapping("/auth/direcciones")
@CrossOrigin("http://localhost:3000")
@RequiredArgsConstructor
public class DireccionController {
    private final DireccionService serv;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DireccionResponse create(
            @RequestParam Long idUser,
            @RequestBody DireccionRequest req
    ) {
        return serv.save(req, idUser);
    }

    @GetMapping("/user/{idUser}")
    @ResponseStatus(HttpStatus.OK)
    public List<DireccionResponse> getByUser(@PathVariable Long idUser) {
        return serv.findByUser(idUser);
    }

    // --- 👇👇👇 ¡¡¡LA PUERTA QUE HEMOS ESTADO BUSCANDO!!! 👇👇👇 ---
    /**
     * Endpoint para eliminar una dirección individualmente.
     * @param idDireccion El ID de la dirección a eliminar, obtenido de la URL.
     * @return Una respuesta HTTP 204 No Content si el borrado es exitoso.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Devuelve 204 si tiene éxito
    public void delete(@PathVariable("id") Long idDireccion) {
        // Llama al método 'delete' que YA EXISTE en tu DireccionServiceImpl.
        // Toda tu lógica de validación se ejecutará.
        serv.delete(idDireccion);
    }
    // --- -------------------------------------------------------- ---
}
