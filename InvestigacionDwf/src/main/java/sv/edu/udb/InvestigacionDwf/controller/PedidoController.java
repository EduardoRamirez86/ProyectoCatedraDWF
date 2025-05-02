// src/main/java/sv/edu/udb/InvestigacionDwf/controller/PedidoController.java
package sv.edu.udb.InvestigacionDwf.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.InvestigacionDwf.dto.request.PedidoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.PedidoResponse;
import sv.edu.udb.InvestigacionDwf.service.PedidoService;

import java.util.List;

@RestController
@RequestMapping(path = "auth/pedido")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping("/checkout")
    public ResponseEntity<PedidoResponse> checkout(@RequestBody PedidoRequest req) {
        PedidoResponse response = pedidoService.checkout(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/user/{idUser}")
    public ResponseEntity<List<PedidoResponse>> getByUser(@PathVariable Long idUser) {
        List<PedidoResponse> list = pedidoService.findAllByUser(idUser);
        return ResponseEntity.ok(list);
    }
}

