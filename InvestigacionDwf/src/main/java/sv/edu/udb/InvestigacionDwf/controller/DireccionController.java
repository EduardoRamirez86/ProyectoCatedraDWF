package sv.edu.udb.InvestigacionDwf.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
// --- 👇👇👇 ¡¡¡IMPORTA ESTO!!! ¡¡¡ES CLAVE!!! 👇👇👇 ---
import org.springframework.http.ResponseEntity;
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

    // --- 👇👇👇 ¡¡¡EL MÉTODO QUE HA CAUSADO TODO ESTE SUFRIMIENTO, AHORA SÍ!!! 👇👇👇 ---
    /**
     * Endpoint para eliminar una dirección por su ID.
     * Recibe el ID desde la URL.
     * Llama al método delete del servicio, que ya contiene la lógica de validación.
     * @param idDireccion El ID de la dirección a eliminar.
     * @return Una respuesta HTTP 204 No Content si el borrado es exitoso.
     *         El ExceptionHandler global se encargará de los errores (404 si no existe, 400 si el ID es nulo).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long idDireccion) {
        // Aquí llamamos al método delete de tu DireccionServiceImpl.
        // Toda la lógica que escribiste (if Objects.isNull, if !repo.existsById)
        // se ejecutará dentro de esta llamada.
        serv.delete(idDireccion);

        // Si serv.delete() lanza una excepción, no llegará a esta línea.
        // Si no lanza excepción, significa que el borrado fue exitoso.
        // Devolvemos 204 No Content, que es la mejor práctica para un DELETE exitoso.
        return ResponseEntity.noContent().build();
    }
    // --- -------------------------------------------------------------------- ---
}
