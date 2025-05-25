// src/main/java/sv/edu/udb/InvestigacionDwf/controller/DireccionController.java
package sv.edu.udb.InvestigacionDwf.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.CREATED) // Indica que se devuelve un 201 Created
    public DireccionResponse create(
            @RequestParam Long idUser,
            @RequestBody DireccionRequest req
    ) {
        return serv.save(req, idUser);
    }

    @GetMapping("/user/{idUser}")
    @ResponseStatus(HttpStatus.OK) // Indica que se devuelve un 200 OK
    public List<DireccionResponse> getByUser(@PathVariable Long idUser) {
        return serv.findByUser(idUser);
    }
}


